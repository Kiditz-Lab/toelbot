import os
from async_odoo_client import AsyncOdooClient
from mcp.server.fastmcp import FastMCP

mcp = FastMCP(
    "odoo-sales",
    dependencies=["aiohttp", "aiohttp_xmlrpc"],
)

# --- Customer --- #


@mcp.tool(description="Search customers by email or name")
async def search_customer_for_sales_team(query: str) -> dict:
    if not query:
        return {"error": "Please provide a search query (email or name)"}

    client = AsyncOdooClient()
    await client.authenticate()

    try:
        customers = await client.search_read(
            model="res.partner",
            domain=["|", ("email", "ilike", query), ("name", "ilike", query)],
            fields=["id", "name", "email"],
            limit=10,
        )
        if not customers:
            return {"message": "No customers found"}
        return {"customers": customers}
    finally:
        await client.close()


@mcp.tool(description="Create a new customer in Odoo")
async def create_customer_for_sales_team(
    name: str,
    email: str,
    phone: str = "",
    company_name: str = "",
    street: str = "",
    city: str = "",
    state_id: int = 0,
    country_id: int = 0,
) -> dict:
    client = AsyncOdooClient()
    try:
        await client.authenticate()
        customer_id = await client.create(
            "res.partner",
            {
                "name": name,
                "email": email,
                "phone": phone,
                "company_name": company_name,
                "street": street,
                "city": city,
                "state_id": state_id,
                "country_id": country_id,
            },
        )
        return {"customer_id": customer_id}
    except Exception as e:
        return {"error": f"Failed to create customer: {str(e)}"}
    finally:
        await client.close()


@mcp.tool(description="Update an existing customer in Odoo by email")
async def update_customer_for_sales_team(
    email: str,
    name: str = "",
    phone: str = "",
    company_name: str = "",
    street: str = "",
    city: str = "",
    state_id: int = 0,
    country_id: int = 0,
) -> dict:
    client = AsyncOdooClient()
    try:
        await client.authenticate()

        customer_ids = await client.search("res.partner", [["email", "=", email]])
        if not customer_ids:
            return {"error": f"No customer found with email '{email}'"}

        customer_id = customer_ids[0]
        fields_to_update = {
            k: v
            for k, v in {
                "name": name,
                "phone": phone,
                "company_name": company_name,
                "street": street,
                "city": city,
                "state_id": state_id,
                "country_id": country_id,
            }.items()
            if v
        }

        success = await client.write("res.partner", [customer_id], fields_to_update)
        return {"success": success}
    except Exception as e:
        return {"error": f"Failed to update customer: {str(e)}"}
    finally:
        await client.close()


@mcp.tool(description="Delete a customer by email from Odoo")
async def delete_customer_by_email_for_sales_team(email: str) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()

    customer_ids = await client.search("res.partner", [["email", "=", email]], limit=1)
    if not customer_ids:
        await client.close()
        return {"error": f"Customer with email '{email}' not found."}

    success = await client.unlink("res.partner", customer_ids)
    await client.close()
    return {"success": success}


@mcp.tool(description="Get detailed information about a customer by email")
async def get_customer_by_email_for_sales_team(email: str) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()
    customer_ids = await client.search("res.partner", [["email", "=", email]])
    if not customer_ids:
        await client.close()
        return {"error": "Customer not found"}
    customer = await client.read(
        "res.partner",
        customer_ids,
        fields=[
            "id",
            "name",
            "email",
            "phone",
            "company_name",
            "street",
            "city",
            "state_id",
            "country_id",
        ],
    )
    await client.close()
    return customer[0]


@mcp.tool(
    description="Create a  sales order quotation for a customer using email and product name"
)
async def create_sales_order_for_sales_team(
    customer_email: str,
    product_name: str,
    quantity: int,
) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()

    customer_ids = await client.search(
        "res.partner", [["email", "=", customer_email]], limit=1
    )
    if not customer_ids:
        await client.close()
        return {"error": f"Customer with email '{customer_email}' not found."}

    template_ids = await client.search(
        "product.template", [["name", "ilike", product_name.strip()]], limit=1
    )
    if not template_ids:
        await client.close()
        return {"error": f"Product '{product_name}' not found."}

    product_ids = await client.search(
        "product.product", [["product_tmpl_id", "=", template_ids[0]]], limit=1
    )
    if not product_ids:
        await client.close()
        return {"error": f"No variants found for product '{product_name}'."}

    quotation_id = await client.create(
        "sale.order",
        {
            "partner_id": customer_ids[0],
            "order_line": [
                (0, 0, {"product_id": product_ids[0], "product_uom_qty": quantity})
            ],
        },
    )

    quotation = await client.read("sale.order", [quotation_id], fields=["name"])
    await client.close()
    return {"quotation_name": quotation[0]["name"]}


@mcp.tool(description="Confirm a sales order in Odoo using the order number")
async def confirm_sales_order_by_name_for_sales_team(order_name: str) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()

    # Search for the sales order by name
    order_ids = await client.search("sale.order", [["name", "=", order_name]], limit=1)
    if not order_ids:
        await client.close()
        return {"error": f"Sales order '{order_name}' not found."}

    # Read the current state of the order
    order = await client.read("sale.order", order_ids[0], fields=["state"])
    if not order:
        await client.close()
        return {"error": f"Could not read data for sales order '{order_name}'."}

    # If the order is already confirmed, return a message
    if order[0]["state"] == "sale":
        await client.close()
        return {"message": f"Sales order '{order_name}' is already confirmed."}

    # Confirm the order using action_confirm
    success = await client.execute_kw("sale.order", "action_confirm", order_ids)
    await client.close()

    if success:
        return {
            "message": f"Sales order '{order_name}' has been successfully confirmed."
        }
    else:
        return {"error": f"Failed to confirm sales order '{order_name}'."}


@mcp.tool(description="Update a sales order in Odoo using the order number")
async def update_sales_order_for_sales_team(order_number: str, state: str) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()

    order_ids = await client.search(
        "sale.order", [["name", "=", order_number]], limit=1
    )
    if not order_ids:
        await client.close()
        return {"error": f"Sales order with name '{order_number}' not found."}
    order_id = order_ids[0]

    success = await client.write("sale.order", [order_id], {"state": state})
    await client.close()
    return {"success": success}


@mcp.tool(description="Delete a sales order from Odoo using order number")
async def delete_sales_order_for_sales_team(order_number: str) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()
    order_ids = await client.search(
        "sale.order", [["name", "=", order_number]], limit=1
    )
    if not order_ids:
        await client.close()
        return {"error": f"Sales order with number '{order_number}' not found."}
    success = await client.unlink("sale.order", order_ids)
    await client.close()
    return {"success": success}


@mcp.tool(description="Get detailed information about a sales order by order number")
async def get_sales_order_by_number_for_sales_team(order_number: str) -> dict:
    if not order_number.strip():
        return {"error": "Order number cannot be empty."}
    client = AsyncOdooClient()
    await client.authenticate()
    order_ids = await client.search("sale.order", [["name", "=", order_number]])
    if not order_ids:
        await client.close()
        return {"error": "Sales order not found"}
    order = await client.read(
        "sale.order",
        order_ids,
        fields=["id", "name", "partner_id", "order_line", "state"],
    )
    await client.close()
    return order[0]


@mcp.tool(description="Get all sales orders for a customer using their email")
async def list_sales_orders_by_customer_email_for_sales_team(
    customer_email: str,
) -> dict:
    client = AsyncOdooClient()
    try:
        await client.authenticate()

        if not customer_email.strip():
            return {"error": "Customer email cannot be empty."}

        customer_ids = await client.search(
            "res.partner", [["email", "=", customer_email.strip().lower()]], limit=1
        )
        if not customer_ids:
            return {"error": f"Customer with email '{customer_email}' not found."}

        order_ids = await client.search(
            "sale.order", [["partner_id", "=", customer_ids[0]]]
        )
        orders = await client.read(
            "sale.order",
            order_ids,
            fields=["id", "name", "date_order", "amount_total", "state"],
        )

        return {"orders": orders}
    finally:
        await client.close()


@mcp.tool(
    description="Send sales order email based on order state by order number or customer email"
)
async def send_order_to_email_for_sales_team(
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

@mcp.tool(description="Get invoice details by invoice number")
async def get_invoice_by_number_for_sales_team(invoice_number: str) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()
    invoice_ids = await client.search("account.move", [["name", "=", invoice_number]])
    if not invoice_ids:
        await client.close()
        return {"error": "Invoice not found"}
    invoice = await client.read(
        "account.move",
        invoice_ids,
        fields=[
            "id",
            "name",
            "invoice_date",
            "amount_total",
            "state",
            "move_type",
            "partner_id",
            "invoice_line_ids",
        ],
    )
    await client.close()
    return invoice[0]


@mcp.tool(description="Get all invoices for a customer using their email")
async def list_invoices_by_customer_for_sales_team(customer_email: str) -> list:
    client = AsyncOdooClient()
    await client.authenticate()

    customer_ids = await client.search(
        "res.partner", [["email", "=", customer_email]], limit=1
    )
    if not customer_ids:
        await client.close()
        raise ValueError(f"Customer with email '{customer_email}' not found.")

    invoice_ids = await client.search(
        "account.move",
        [["partner_id", "=", customer_ids[0]], ["move_type", "=", "out_invoice"]],
    )
    invoices = await client.read(
        "account.move",
        invoice_ids,
        fields=["id", "name", "invoice_date", "amount_total", "state"],
    )

    await client.close()
    return invoices


@mcp.tool(description="Create a credit note (refund) for an invoice using invoice name")
async def create_credit_note_for_sales_team(
    invoice_name: str, reason: str = ""
) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()

    invoice_ids = await client.search(
        "account.move", [["name", "=", invoice_name]], limit=1
    )
    if not invoice_ids:
        await client.close()
        raise ValueError(f"Invoice with name '{invoice_name}' not found.")

    refund_id = await client.create(
        "account.move",
        {
            "move_type": "out_refund",
            "reversed_entry_id": invoice_ids[0],
            "ref": reason,
        },
    )

    await client.close()
    return {"refund_id": refund_id}


@mcp.tool(description="Filter invoices by date, status, and total amount")
async def filter_invoices_for_sales_team(
    date_from: str = "",  # format: YYYY-MM-DD
    date_to: str = "",
    status: str = "",  # e.g. 'draft', 'posted', 'cancel'
    min_total: float = 0,
    max_total: float = 0,
) -> list:
    client = AsyncOdooClient()
    await client.authenticate()

    domain = []

    if date_from:
        domain.append(["invoice_date", ">=", date_from])
    if date_to:
        domain.append(["invoice_date", "<=", date_to])
    if status:
        domain.append(["state", "=", status])
    if min_total > 0:
        domain.append(["amount_total", ">=", min_total])
    if max_total > 0:
        domain.append(["amount_total", "<=", max_total])

    invoice_ids = await client.search("account.move", domain)
    invoices = await client.read(
        "account.move",
        invoice_ids,
        fields=["id", "name", "invoice_date", "amount_total", "state"],
    )

    await client.close()
    return invoices


@mcp.tool(description="Create a new product in Odoo using name and category")
async def create_product_for_sales_team(
    product_name: str,
    list_price: float = 0.0,
    product_type: str = "consu",
    category_name: str = "",
) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()

    # Resolve or create category
    category_id = 0
    if category_name:
        category_ids = await client.search(
            "product.category", [["name", "=", category_name]]
        )
        category_id = (
            category_ids[0]
            if category_ids
            else await client.create("product.category", {"name": category_name})
        )

    template_id = await client.create(
        "product.template",
        {
            "name": product_name,
            "list_price": list_price,
            "type": product_type,
            "categ_id": category_id,
        },
    )

    product = await client.read(
        "product.template",
        [template_id],
        fields=["id", "name", "list_price", "type", "categ_id"],
    )
    await client.close()
    return product[0]


@mcp.tool(description="Get product details by name")
async def get_product_by_name_for_sales_team(product_name: str) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()

    product_ids = await client.search("product.template", [["name", "=", product_name]])
    if not product_ids:
        await client.close()
        raise ValueError(f"No product found with name '{product_name}'")

    product = await client.read(
        "product.template",
        product_ids,
        fields=["id", "name", "list_price", "type", "categ_id"],
    )
    await client.close()
    return product[0]


@mcp.tool(description="Update product details by name")
async def update_product_by_name_for_sales_team(
    product_name: str,
    new_name: str = "",
    new_price: float = 0.0,
    new_category: str = "",
) -> bool:
    client = AsyncOdooClient()
    await client.authenticate()

    product_ids = await client.search("product.template", [["name", "=", product_name]])
    if not product_ids:
        await client.close()
        raise ValueError(f"No product found with name '{product_name}'")

    product_id = product_ids[0]
    values = {}

    if new_name:
        values["name"] = new_name
    if new_price:
        values["list_price"] = new_price
    if new_category:
        category_ids = await client.search(
            "product.category", [["name", "=", new_category]]
        )
        category_id = (
            category_ids[0]
            if category_ids
            else await client.create("product.category", {"name": new_category})
        )
        values["categ_id"] = category_id

    success = await client.write("product.template", [product_id], values)
    await client.close()
    return success


@mcp.tool(description="Search products by name, description, and limit")
async def search_products_for_sales_team(
    query: str = "", limit: int = 10
) -> list[dict]:
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


if __name__ == "__main__":
    mcp.run(transport="stdio")
