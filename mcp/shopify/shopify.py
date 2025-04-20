import requests
from typing import Optional, Dict, Any


class ShopifyClient:
    def __init__(self, store_url: str, access_token: str, api_version: str = "2024-04"):
        self.store_url = store_url
        self.access_token = access_token
        self.api_version = api_version
        self.base_url = f"https://{self.store_url}/admin/api/{self.api_version}"
        self.headers = {
            "Content-Type": "application/json",
            "X-Shopify-Access-Token": self.access_token,
        }

    def _request(
        self,
        method: str,
        endpoint: str,
        params: Optional[Dict[str, Any]] = None,
        data: Optional[Dict[str, Any]] = None,
    ) -> Dict[str, Any]:
        url = f"{self.base_url}/{endpoint}"
        response = requests.request(
            method, url, headers=self.headers, params=params, json=data
        )
        response.raise_for_status()
        if response.content:
            return response.json()
        return {}
   
    def _graphql_request(self, query: str, variables: dict = None) -> dict:
        payload = {"query": query, "variables": variables}
        # Use _request with POST, no params, and send payload as data
        return self._request(
            method="POST",
            endpoint="graphql.json", 
            data=payload,
        )



    def search_products(self, search_title=None, limit=10, after_cursor=None):
        product_images_fragment = """
        fragment productImagesFragment on Image {
            id
            src
            altText
        }
        """
        product_variants_fragment = """
        fragment productVariantsFragment on ProductVariant {
            id
            title
            price
            sku
            inventoryQuantity
            availableForSale
        }
        """
        product_fragment = """
        id
        handle
        title
        description
        publishedAt
        updatedAt
        options {
            id
            name
            values
        }
        images(first: 20) {
            edges {
            node {
                ...productImagesFragment
            }
            }
        }
        variants(first: 250) {
            edges {
            node {
                ...productVariantsFragment
            }
            }
        }
        """
        # after_cursor_clause = ', after: $afterCursor' if after_cursor else ""
        graphql_query = f"""
        {product_images_fragment}
        {product_variants_fragment}
        query searchProducts($first: Int!, $title: String, $afterCursor: String) {{
            products(first: $first, query: $title, after: $afterCursor) {{
            edges {{
                node {{
                {product_fragment}
                }}
            }}
            pageInfo {{
                hasNextPage
                endCursor
            }}
            }}
        }}
        """

        title_filter = f"title:*{search_title}*" if search_title else ""
        variables = {
            "first": limit,
            "title": title_filter,
            "afterCursor": after_cursor or None,
        }

        result = self._graphql_request(graphql_query, variables)
        
        # Debugging the response

        data = result.get('data', {}).get('products', {})
        edges = data.get('edges', [])
        page_info = data.get('pageInfo', {})

        products = [edge['node'] for edge in edges]
        next_cursor = page_info.get('endCursor') if page_info.get('hasNextPage') else None

        return {"products": products, "next": next_cursor}




    # Products
    def create_product(self, product_data: dict):
        return self._request("POST", "products.json", data={"product": product_data})

    def get_product(self, product_id: int):
        return self._request("GET", f"products/{product_id}.json")

    def update_product(self, product_id: int, update_data: dict):
        return self._request(
            "PUT", f"products/{product_id}.json", data={"product": update_data}
        )

    def delete_product(self, product_id: int):
        self._request("DELETE", f"products/{product_id}.json")
        return True

    def list_products(self, params: Optional[Dict[str, Any]] = None):
        return self._request("GET", "products.json", params=params)

    # Orders
    def list_orders(self, params: Optional[Dict[str, Any]] = None):
        return self._request("GET", "orders.json", params=params)

    def get_order(self, order_id: int):
        return self._request("GET", f"orders/{order_id}.json")

    # Customers
    def list_customers(self, params: Optional[Dict[str, Any]] = None):
        return self._request("GET", "customers.json", params=params)

    def get_customer(self, customer_id: int):
        return self._request("GET", f"customers/{customer_id}.json")

    def create_customer(self, customer_data: dict):
        return self._request("POST", "customers.json", data={"customer": customer_data})

    def update_customer(self, customer_id: int, update_data: dict):
        return self._request(
            "PUT", f"customers/{customer_id}.json", data={"customer": update_data}
        )

    def delete_customer(self, customer_id: int):
        self._request("DELETE", f"customers/{customer_id}.json")
        return True

    # Collections
    def list_custom_collections(self, params: Optional[Dict[str, Any]] = None):
        return self._request("GET", "custom_collections.json", params=params)

    def create_custom_collection(self, collection_data: dict):
        return self._request(
            "POST",
            "custom_collections.json",
            data={"custom_collection": collection_data},
        )

    # Metafields
    def list_metafields(self, params: Optional[Dict[str, Any]] = None):
        return self._request("GET", "metafields.json", params=params)

    # Inventory
    def list_inventory_items(self, params: Optional[Dict[str, Any]] = None):
        return self._request("GET", "inventory_items.json", params=params)

    # Discounts
    def list_price_rules(self, params: Optional[Dict[str, Any]] = None):
        return self._request("GET", "price_rules.json", params=params)

    def create_price_rule(self, price_rule_data: dict):
        return self._request(
            "POST", "price_rules.json", data={"price_rule": price_rule_data}
        )

    def create_discount_code(self, price_rule_id: int, discount_data: dict):
        return self._request(
            "POST",
            f"price_rules/{price_rule_id}/discount_codes.json",
            data={"discount_code": discount_data},
        )

    # Generic request
    def request(
        self,
        method: str,
        endpoint: str,
        params: Optional[Dict[str, Any]] = None,
        data: Optional[Dict[str, Any]] = None,
    ) -> Dict[str, Any]:
        """
        Direct access to Shopify API if you need a custom request not defined.
        """
        return self._request(method, endpoint, params=params, data=data)
