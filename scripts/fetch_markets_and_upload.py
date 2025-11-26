import requests
import json
from dacite import from_dict
from typing import Optional, List
from dataclasses import dataclass, asdict
from datetime import datetime

import ast
from py_clob_client.client import ClobClient
from dateutil.parser import isoparse

# mapper
@dataclass
class Market:
    question: Optional[str] = None
    conditionId: Optional[str] = None
    slug: Optional[str] = None
    startDate: Optional[str] = None
    endDate: Optional[str] = None
    outcomes: Optional[str] = None
    outcomePrices: Optional[str] = None
    active: Optional[bool] = None
    enableOrderBook: Optional[bool] = None
    volumeNum: Optional[float] = None
    volume24hr: Optional[float] = None
    clobTokenIds: Optional[str] = None
    acceptingOrders: Optional[bool] = None

    def __post_init__(self):
        self.outcomes=ast.literal_eval(self.outcomes)
        self.outcomePrices=list(map(lambda price: float(price), ast.literal_eval(self.outcomePrices)))
        self.clobTokenIds=ast.literal_eval(self.clobTokenIds)
        self.startDate=isoparse(self.startDate)
        self.endDate=isoparse(self.endDate)

def map_markets_dict_to_markets(raw_markets):
    mapped_markets = []
    for raw_market in raw_markets:
        mapped_markets.append(from_dict(data_class=Market, data=raw_market))
    return mapped_markets

closed='false'
volume_num_min='200000'
end_date_min='2025-12-01'
limit='99999'

def get_markets(**args):
    url = 'https://gamma-api.polymarket.com/markets'
    response = requests.get(url, params=args)
    return response.json()

mapped_markets = map_markets_dict_to_markets(markets)

def serializer(obj):
    if isinstance(obj, datetime):
        return obj.isoformat(timespec='milliseconds')
    raise TypeError

serialized = json.dumps([asdict(market) for market in mapped_markets], default=serializer)
payload = json.loads(serialized)

requests.post("http://localhost:8080/market", json=payload)
