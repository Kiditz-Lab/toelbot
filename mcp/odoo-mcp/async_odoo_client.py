import os
import asyncio
from dotenv import load_dotenv
from aiohttp import ClientSession
from aiohttp_xmlrpc.client import ServerProxy


class AsyncOdooClient:
    def __init__(self):
        load_dotenv()
        self.url = os.getenv("ODOO_URL")
        self.db = os.getenv("ODOO_DB")
        self.email = os.getenv("ODOO_EMAIL")
        self.api_key = os.getenv("ODOO_API_KEY")

        if not all([self.url, self.db, self.email, self.api_key]):
            raise ValueError(
                "Missing one or more Odoo credentials in environment variables."
            )

        self.session = ClientSession()
        self.common = ServerProxy(f"{self.url}/xmlrpc/2/common", client=self.session)
        self.models = ServerProxy(f"{self.url}/xmlrpc/2/object", client=self.session)
        self.uid = None

    async def authenticate(self):
        self.uid = await self.common.authenticate(self.db, self.email, self.api_key, {})
        if not self.uid:
            raise Exception("Authentication failed. Please check your credentials.")

    async def search(self, model, domain, offset=0, limit=10, order=None):
        return await self.models.execute_kw(
            self.db,
            self.uid,
            self.api_key,
            model,
            "search",
            [domain],
            {"offset": offset, "limit": limit, "order": order},
        )

    async def search_read(
        self, model, domain, fields=None, offset=0, limit=10, order=None
    ):
        return await self.models.execute_kw(
            self.db,
            self.uid,
            self.api_key,
            model,
            "search_read",
            [domain],
            {"fields": fields or [], "offset": offset, "limit": limit, "order": order},
        )

    async def read(self, model, ids, fields=None):
        return await self.models.execute_kw(
            self.db,
            self.uid,
            self.api_key,
            model,
            "read",
            [ids],
            {"fields": fields or []},
        )

    async def create(self, model, data):
        return await self.models.execute_kw(
            self.db, self.uid, self.api_key, model, "create", [data]
        )

    async def write(self, model, ids, data):
        return await self.models.execute_kw(
            self.db, self.uid, self.api_key, model, "write", [ids, data]
        )

    async def unlink(self, model, ids):
        return await self.models.execute_kw(
            self.db, self.uid, self.api_key, model, "unlink", [ids]
        )

    async def execute_kw(self, model, method, *args, **kwargs):
        return await self.models.execute_kw(
            self.db, self.uid, self.api_key, model, method, list(args), kwargs
        )

    async def fields_get(self, model, attributes=None):
        return await self.models.execute_kw(
            self.db,
            self.uid,
            self.api_key,
            model,
            "fields_get",
            [],
            {"attributes": attributes or ["string", "type", "required", "readonly"]},
        )

    async def close(self):
        await self.session.close()


if __name__ == "__main__":

    async def main():
        client = AsyncOdooClient()
        await client.authenticate()
        fields = await client.fields_get("product.category")
        for field, meta in fields.items():
            print(f"{field} ({meta['type']}): {meta['string']}")
        client.close()

    asyncio.run(main())
