from typing import Optional
from pydantic import BaseModel


class ProductSearchInput(BaseModel):
    name: Optional[str] = None
    default_code: Optional[str] = None
    limit: int = 10


class ProductResult(BaseModel):
    id: int
    name: str
    list_price: float
