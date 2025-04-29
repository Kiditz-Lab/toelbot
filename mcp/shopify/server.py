import os
from typing import Optional
from mcp.server.fastmcp import FastMCP
from input import (
    CustomCollectionCreate,
    CustomerCreate,
    CustomerUpdate,
    ProductCreate,
    ProductUpdate,
)
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
    return {"products": products}


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


@mcp.tool(
    description="""
Update an existing product on Shopify.

To update a product, you need two things:
1. The `product_id` (the ID of the product you want to update) — provided separately.
2. The `update_data` (the fields you want to update) — provided as a JSON object.

You can update any fields you want, such as:
- `title`: Update the product's name.
- `body_html`: Update the rich text description (supports HTML formatting).
- `vendor`: Update the brand or manufacturer name.
- `product_type`: Update the category of the product.
- `tags`: Update the list of tags.
- `variants`: Update or add product variants (sizes, colors, etc.).
- `images`: Update product images (each needs a direct URL `src`).

Example:

Input:
- `product_id`: "1234567890"
- `update_data`:
  {
    "title": "Awesome T-Shirt (Updated)",
    "body_html": "<strong>Now even softer!</strong>",
    "tags": ["Summer", "Cotton", "Men", "Updated"],
    "variants": [
      {
        "option1": "Medium",
        "price": "21.99",
        "sku": "TSHIRT-MEDIUM"
      }
    ],
    "images": [
      {
        "src": "https://your-new-image-url.com/shirt-new.jpg"
      }
    ]
  }

Important:
- You don't need to send all fields — just the fields you want to change.
- If updating images, ensure the image URLs are public.
- If updating variants, you can either modify existing ones (with their `id`) or add new ones.

This function only updates the fields you provide (fields not included stay unchanged).
"""
)
def update_product(product_id: str, update_data: ProductUpdate) -> dict:
    client = get_shopify_client()
    product = client.update_product(
        product_id, update_data.model_dump(exclude_none=True)
    )
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


@mcp.tool(
    description="""
Create a new customer on Shopify.

To create a customer, provide the customer's information as a JSON object.

You can include fields such as:
- `first_name`: Customer's first name.
- `last_name`: Customer's last name.
- `email`: Customer's email address (required and must be valid).
- `phone`: Customer's phone number (optional).
- `verified_email`: Whether the email is verified (true or false).
- `tags`: Tags to help categorize or group customers (optional).
- `note`: An internal note about the customer (optional).

Example input:

{
  "first_name": "John",
  "last_name": "Doe",
  "email": "john.doe@example.com",
  "phone": "+15555555555",
  "verified_email": true,
  "tags": ["VIP", "Newsletter Subscriber"],
  "note": "Loyal customer since 2020"
}

Important:
- `email` must be unique across all customers.
- If `verified_email` is true, Shopify treats the email as confirmed.
- Tags help for segmentation (e.g., for marketing purposes).

"""
)
def create_customer(customer_data: CustomerCreate) -> dict:
    client = get_shopify_client()
    customer = client.create_customer(customer_data)
    return {"customer": customer}


@mcp.tool(
    description="""
Update an existing customer on Shopify.

To update a customer, you need two things:
1. The `customer_id` (the ID of the customer you want to update) — provided separately.
2. The `update_data` (the fields you want to update) — provided as a JSON object.

You can update fields such as:
- `first_name`
- `last_name`
- `email` (must still be valid and unique)
- `phone`
- `verified_email`
- `tags`
- `note`

Example input:

- `customer_id`: "9876543210"
- `update_data`:
  {
    "first_name": "Jane",
    "last_name": "Smith",
    "phone": "+16666666666",
    "tags": ["Returning Customer", "Special Discount"]
  }

Important:
- You only need to include the fields you want to change.
- Fields left out will not be modified.
- Email updates must still meet Shopify's requirements (valid, unique).

This function updates only the specified fields and leaves others unchanged.

"""
)
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


@mcp.tool(
    description="""
Create a new custom collection on Shopify.

To create a custom collection, you need to provide at least the collection's title.
You can optionally include a description, image, handle, and sorting preferences. Custom collections allow you to group and categorize your products, making it easier to organize and present them on your store.

- `title` (required): The name of the custom collection.
- `body_html` (optional): A rich text description of the collection.
- `image` (optional): A URL to an image that represents the collection.
- `handle` (optional): A unique URL-friendly identifier for the collection (defaults to the title).
- `sort_order` (optional): The sort order of the collection (e.g., "manual", "best-selling", "price-ascending", etc.).

Example:
{
  "title": "Summer Collection",
  "body_html": "<strong>A collection of summer clothing</strong>",
  "image": {
    "src": "https://your-image-url.com/summer-collection.jpg"
  },
  "handle": "summer-collection",
  "sort_order": "manual"
}

Make sure the `image` URL is publicly accessible, and `handle` is unique across collections. The `sort_order` can be left as the default "manual" unless you have a specific sorting preference.
"""
)
def create_custom_collection(collection_data: CustomCollectionCreate) -> dict:
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


@mcp.tool(
    description="""
Create a new discount code under a price rule on Shopify.

To create a discount code, you must provide:
- `price_rule_id` (required): The ID of the existing price rule under which the discount code will be created.
- `code` (required): A unique discount code string that customers will use at checkout.

Example:
{
  "code": "SUMMER2025"
}

Make sure the discount code is unique and meets Shopify's discount code requirements (e.g., no spaces, case insensitive).
"""
)
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
    print(search_products(query="snow", limit=10))
    # mcp.run()


if __name__ == "__main__":
    main()
