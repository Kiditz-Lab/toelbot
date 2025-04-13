import os
import xmlrpc.client
from dotenv import load_dotenv

class OdooClient:
    def __init__(self):
        load_dotenv()
        self.url = os.getenv("ODOO_URL")
        self.db = os.getenv("ODOO_DB")
        self.email = os.getenv("ODOO_EMAIL")
        self.api_key = os.getenv("ODOO_API_KEY")

        if not all([self.url, self.db, self.email, self.api_key]):
            raise ValueError("Missing one or more Odoo credentials in environment variables.")

        self.common = xmlrpc.client.ServerProxy(f"{self.url}/xmlrpc/2/common")
        self.uid = self.common.authenticate(self.db, self.email, self.api_key, {})
        if not self.uid:
            raise Exception("Authentication failed. Please check your credentials.")

        self.models = xmlrpc.client.ServerProxy(f"{self.url}/xmlrpc/2/object", allow_none=True)

    def search(self, model, domain, offset=0, limit=10, order=None):
        return self.models.execute_kw(
            self.db, self.uid, self.api_key,
            model, 'search',
            [domain],
            {'offset': offset, 'limit': limit, 'order': order}
        )

    def search_read(self, model, domain, fields=None, offset=0, limit=10, order=None):
        return self.models.execute_kw(
            self.db, self.uid, self.api_key,
            model, 'search_read',
            [domain],
            {
                'fields': fields or [],
                'offset': offset,
                'limit': limit,
                'order': order
            }
        )

    def read(self, model, ids, fields=None):
        return self.models.execute_kw(
            self.db, self.uid, self.api_key,
            model, 'read',
            [ids],
            {'fields': fields or []}
        )

    def create(self, model, data):
        return self.models.execute_kw(
            self.db, self.uid, self.api_key,
            model, 'create',
            [data]
        )

    def write(self, model, ids, data):
        return self.models.execute_kw(
            self.db, self.uid, self.api_key,
            model, 'write',
            [ids, data]
        )

    def unlink(self, model, ids):
        return self.models.execute_kw(
            self.db, self.uid, self.api_key,
            model, 'unlink',
            [ids]
        )

    def exec_workflow(self, model, id, signal):
        return self.models.exec_workflow(
            self.db, self.uid, self.api_key,
            model, signal, id
        )

    def execute_kw(self, model, method, *args, **kwargs):
        return self.models.execute_kw(
            self.db, self.uid, self.api_key,
            model, method,
            list(args),
            kwargs
        )
    
    def fields_get(self, model, attributes=None):
        return self.models.execute_kw(
            self.db, self.uid, self.api_key,
            model, 'fields_get',
            [],
            {'attributes': attributes or ['string', 'type', 'required', 'readonly']}
        )

if __name__ == "__main__":
    odoo = OdooClient()
    product_fields = odoo.fields_get('product.template')
    for field, meta in product_fields.items():
        print(f"{field} ({meta['type']}): {meta['string']}")
    
