#!/bin/bash 

mkdir server
cd server
git clone https://github.com/plaid/quickstart.git
cd quickstart/node
npm install

# Start the Quickstart with your API keys from the Dashboard
# https://dashboard.plaid.com/account/keys
#
# PLAID_PRODUCTS is a comma-separated list of products to use when
# initializing Link, see https://plaid.com/docs/#item-product-access
# for complete list.

# PLAID_COUNTRY_CODES is a comma-separated list of countries to use when
# initializing Link, see plaid.com/docs/faq/#does-plaid-support-international-bank-accounts-
# for a complete list

PLAID_CLIENT_ID=$1 \
PLAID_SECRET=$2 \
PLAID_PUBLIC_KEY=$3 \
PLAID_ENV='sandbox' \
PLAID_PRODUCTS='transactions' \
PLAID_COUNTRY_CODES='US' \
node index.js

# Go to http://localhost:8000
