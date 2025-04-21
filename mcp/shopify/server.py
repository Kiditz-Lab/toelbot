import os
from typing import Optional
from mcp.server.fastmcp import FastMCP
from input import CustomerCreate, CustomerUpdate, ProductCreate, ProductUpdate
from shopify import ShopifyClient
from dotenv import load_dotenv
mcp = FastMCP(
    "shopify",
    dependencies=["requests", "pydantic[email]"],
)


def get_shopify_client() -> ShopifyClient:
    load_dotenv()
    store_url = os.getenv("SHOPIFY_STORE_URL")
    access_token = os.getenv("SHOPIFY_ACCESS_TOKEN")
    if not store_url or not access_token:
        raise ValueError(
            "SHOPIFY_STORE_URL and SHOPIFY_ACCESS_TOKEN must be set in environment variables."
        )
    return ShopifyClient(store_url, access_token)


@mcp.tool(description="List products from Shopify")
def list_products(limit: int = 10) -> dict:
    client = get_shopify_client()
    products = client.list_products(params={"limit": limit})
    return {"products": products}


@mcp.tool(description="Search products by title and limit")
def search_products(query: str = "", limit: int = 10) -> dict:
    client = get_shopify_client()
    products = client.search_products(search_title=query, limit=limit)
    return products


@mcp.tool(description="Get product by ID from Shopify")
def get_product(product_id: str) -> dict:
    client = get_shopify_client()
    product = client.get_product(product_id)
    return {"product": product}


@mcp.tool(
    description="""
Create a new product on Shopify.

To create a product, you must provide at least the product's title.
You can optionally include a description (body_html), vendor name, product type, tags, variants (such as size or color options), and images.

- `title` (required): The name of the product.
- `body_html` (optional): A rich text description of the product.
- `vendor` (optional): The name of the brand or manufacturer.
- `product_type` (optional): A category or type for the product.
- `tags` (optional): A list of tags to help organize or filter the product.
- `variants` (optional): List of different product versions (like sizes or colors). Each variant must include at least `option1` (name) and `price`.
- `images` (optional): List of images for the product. Each image requires a direct URL (`src`).

Example:
{
  "title": "Awesome T-Shirt",
  "body_html": "<strong>Good quality cotton shirt</strong>",
  "vendor": "MyBrand",
  "product_type": "Shirts",
  "tags": ["Summer", "Cotton", "Men"],
  "variants": [
    {
      "option1": "Small",
      "price": "19.99",
      "sku": "TSHIRT-SMALL"
    }
  ],
  "images": [
    {
      "src": "https://your-image-url.com/shirt.jpg"
    }
  ]
}

Make sure URLs for images are accessible (publicly hosted) and that fields like price are strings (not numbers).
"""
)
def create_product(product: ProductCreate) -> dict:
    client = get_shopify_client()
    product = client.create_product(product.model_dump(exclude_none=True))
    return {"product": product}


@mcp.tool(description="Update an existing product on Shopify")
def update_product(product_id: str, update_data: ProductUpdate) -> dict:
    client = get_shopify_client()
    product = client.update_product(product_id, update_data.model_dump(exclude_none=True))
    return {"product": product}


@mcp.tool(description="Delete a product by ID from Shopify")
def delete_product(product_id: str) -> dict:
    client = get_shopify_client()
    success = client.delete_product(product_id)
    return {"deleted": success}


# --- Orders --- #


@mcp.tool(description="List orders from Shopify")
def list_orders(limit: int = 10) -> dict:
    client = get_shopify_client()
    orders = client.list_orders(params={"limit": limit})
    return {"orders": orders}


@mcp.tool(description="Get order by ID from Shopify")
def get_order(order_id: str) -> dict:
    client = get_shopify_client()
    order = client.get_order(order_id)
    return {"order": order}


# --- Customers --- #


@mcp.tool(description="List customers from Shopify")
def list_customers(limit: int = 10) -> dict:
    client = get_shopify_client()
    customers = client.list_customers(params={"limit": limit})
    return {"customers": customers}


@mcp.tool(description="Get customer by ID from Shopify")
def get_customer(customer_id: str) -> dict:
    client = get_shopify_client()
    customer = client.get_customer(customer_id)
    return {"customer": customer}


@mcp.tool(description="Create a new customer on Shopify")
def create_customer(customer_data: CustomerCreate) -> dict:
    client = get_shopify_client()
    customer = client.create_customer(customer_data)
    return {"customer": customer}


@mcp.tool(description="Update an existing customer on Shopify")
def update_customer(customer_id: str, update_data: CustomerUpdate) -> dict:
    client = get_shopify_client()
    customer = client.update_customer(customer_id, update_data)
    return {"customer": customer}


@mcp.tool(description="Delete a customer by ID from Shopify")
def delete_customer(customer_id: str) -> dict:
    client = get_shopify_client()
    success = client.delete_customer(customer_id)
    return {"deleted": success}


# --- Collections --- #


@mcp.tool(description="List custom collections from Shopify")
def list_custom_collections(limit: int = 10) -> dict:
    client = get_shopify_client()
    collections = client.list_custom_collections(params={"limit": limit})
    return {"collections": collections}


@mcp.tool(description="Create a new custom collection on Shopify")
def create_custom_collection(collection_data: dict) -> dict:
    client = get_shopify_client()
    collection = client.create_custom_collection(collection_data)
    return {"collection": collection}


# --- Metafields --- #


@mcp.tool(description="List metafields from Shopify")
def list_metafields(limit: int = 10) -> dict:
    client = get_shopify_client()
    metafields = client.list_metafields(params={"limit": limit})
    return {"metafields": metafields}


# --- Inventory --- #


@mcp.tool(description="List inventory items from Shopify")
def list_inventory_items(limit: int = 10) -> dict:
    client = get_shopify_client()
    inventory_items = client.list_inventory_items(params={"limit": limit})
    return {"inventory_items": inventory_items}


# --- Discounts --- #


@mcp.tool(description="List price rules from Shopify")
def list_price_rules(limit: int = 10) -> dict:
    client = get_shopify_client()
    price_rules = client.list_price_rules(params={"limit": limit})
    return {"price_rules": price_rules}


@mcp.tool(description="Create a new price rule on Shopify")
def create_price_rule(price_rule_data: dict) -> dict:
    client = get_shopify_client()
    price_rule = client.create_price_rule(price_rule_data)
    return {"price_rule": price_rule}


@mcp.tool(description="Create a new discount code under a price rule on Shopify")
def create_discount_code(price_rule_id: str, discount_data: dict) -> dict:
    client = get_shopify_client()
    discount_code = client.create_discount_code(price_rule_id, discount_data)
    return {"discount_code": discount_code}


# --- Generic Request --- #


@mcp.tool(description="Make a direct custom API request to Shopify")
def custom_shopify_request(
    method: str,
    endpoint: str,
    params: Optional[dict] = None,
    data: Optional[dict] = None,
) -> dict:
    client = get_shopify_client()
    response = client.request(method, endpoint, params=params, data=data)
    return {"response": response}


def main():
    print(search_products(query='snow', limit=10))
    # mcp.run()


if __name__ == "__main__":
    main()
