import os
from mcp.server.fastmcp import FastMCP

from async_odoo_client import AsyncOdooClient

mcp = FastMCP(
    "odoo-ecommerce", dependencies=["aiohttp", "aiohttp_xmlrpc"], instructions=""
)


@mcp.tool(description="Search products by name, description, and limit")
async def search_products_for_ecommerce(query: str = "", limit: int = 10) -> list[dict]:
    client = AsyncOdooClient()
    await client.authenticate()
    records = await client.search_read(
        model="product.template",
        domain=[
            "|",
            ("name", "ilike", query),
            ("description", "ilike", query),
            ("sale_ok", "=", True),
            ("is_published", "=", True),
        ],
        fields=[
            "id",
            "name",
            "default_code",
            "list_price",
            "description",
            "website_url",
            "type",
            "categ_id",
            "currency_id",
            "volume",
            "weight",
            "volume_uom_name",
            "weight_uom_name",
        ],
        limit=limit,
    )
    odoo_url = os.getenv("ODOO_URL").rstrip("/")
    for product in records:
        if product.get("website_url"):
            product["website_url"] = f"{odoo_url}{product['website_url']}"

    await client.close()
    return records


@mcp.tool(description="Check sales order status by order number and customer email")
async def check_order_status_on_ecommerce(order_number: str = "", customer_email: str = "") -> dict:
    client = AsyncOdooClient()
    await client.authenticate()

    if not order_number or not customer_email:
        await client.close()
        return {"message": "Please provide both order number and customer email"}

    # Search customer by email
    partners = await client.search_read(
        model="res.partner",
        domain=[("email", "ilike", customer_email)],
        fields=["id", "name", "email"],
    )

    if not partners:
        await client.close()
        return {"message": "Customer not found"}

    partner_ids = [p["id"] for p in partners]

    # Combine order_number and customer_email in search
    order_domain = [
        ("name", "=", order_number),
        ("partner_id", "in", partner_ids),
    ]

    # Search for the order
    order_data = await client.search_read(
        model="sale.order",
        domain=order_domain,
        fields=[
            "id",
            "name",
            "date_order",
            "state",
            "partner_id",
            "partner_invoice_id",
            "partner_shipping_id",
            "order_line",
            "amount_untaxed",
            "amount_tax",
            "amount_total",
            "payment_term_id",
            "note",
        ],
        limit=1,
    )

    if not order_data:
        await client.close()
        return {"message": "Order not found"}

    order = order_data[0]

    # Fetch order lines (products)
    product_lines = await client.search_read(
        model="sale.order.line",
        domain=[("order_id", "=", order["id"])],
        fields=[
            "product_id",
            "name",
            "product_uom_qty",
            "price_unit",
            "price_total",
            "tax_id",
        ],
    )

    # Fetch delivery info
    deliveries = await client.search_read(
        model="stock.picking",
        domain=[("origin", "=", order["name"])],
        fields=["name", "state", "scheduled_date"],
    )

    # Read partner details
    invoice_partner = await client.read("res.partner", [order["partner_invoice_id"][0]])
    shipping_partner = await client.read(
        "res.partner", [order["partner_shipping_id"][0]]
    )

    result = {
        "order_number": order["name"],
        "order_date": order["date_order"],
        "status": order["state"],
        "billing_address": invoice_partner[0] if invoice_partner else {},
        "shipping_address": shipping_partner[0] if shipping_partner else {},
        "products": [
            {
                "name": line["name"],
                "product": line["product_id"][1] if line["product_id"] else None,
                "quantity": line["product_uom_qty"],
                "unit_price": line["price_unit"],
                "taxes": line["tax_id"],
                "total": line["price_total"],
            }
            for line in product_lines
        ],
        "deliveries": deliveries,
        "amounts": {
            "untaxed": order["amount_untaxed"],
            "tax": order["amount_tax"],
            "total": order["amount_total"],
        },
        "payment_terms": order.get("payment_term_id", [None, ""])[1],
        "terms_and_conditions": order.get("note", ""),
    }

    await client.close()
    return result


@mcp.tool(
    description="Send sales order email based on order state by order number or customer email"
)
async def send_email_for_order_on_ecommerce(
    order_number: str = "", customer_email: str = ""
) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()

    order_domain = []

    if order_number:
        order_domain = [("name", "=", order_number)]
    elif customer_email:
        partners = await client.search_read(
            model="res.partner",
            domain=[("email", "ilike", customer_email)],
            fields=["id", "name", "email"],
        )

        if not partners:
            await client.close()
            return {"message": "Customer not found"}

        partner_ids = [p["id"] for p in partners]
        order_domain = [("partner_id", "in", partner_ids)]
    else:
        await client.close()
        return {"message": "Please provide either order number or customer email"}

    # Fetch the order including its state
    orders = await client.search_read(
        model="sale.order",
        domain=order_domain,
        fields=["id", "name", "state"],
        limit=1,
    )

    if not orders:
        await client.close()
        return {"message": "Order not found"}

    order = orders[0]
    state = order["state"]

    # Fetch all sale.order email templates
    templates = await client.search_read(
        model="mail.template",
        domain=[("model", "=", "sale.order")],
        fields=["id", "name"],
    )

    # Match template based on state
    def find_template_by_keywords(keywords: list[str]):
        keywords = [k.lower() for k in keywords]
        for tpl in templates:
            name = tpl["name"].lower()
            if all(k in name for k in keywords) and "cancel" not in name:
                return tpl["id"]
        return None

    state_to_keywords = {
        "draft": ["quotation"],
        "sent": ["quotation", "sent"],
        "sale": ["order", "confirm"],
        "cancel": ["cancel"],
        "done": ["done", "complete"],
    }

    keywords = state_to_keywords.get(state, [])
    template_id = find_template_by_keywords(keywords)

    if not template_id:
        await client.close()
        return {"message": f"No email template found for order state '{state}'"}

    try:
        await client.execute_kw(
            "mail.template",
            "send_mail",
            template_id,
            order["id"],
            force_send=True,
        )
        await client.close()
        return {"message": f"Email sent for order {order['name']} with state '{state}'"}
    except Exception as e:
        await client.close()
        return {"message": f"Failed to send email: {str(e)}"}


if __name__ == "__main__":
    mcp.run(transport="stdio")
