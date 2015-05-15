import requests
import sys

url = sys.argv[1]
fn = sys.argv[2]

files = {'file': open(fn, 'rb')}
r = requests.post(url, files=files)
print r.text
