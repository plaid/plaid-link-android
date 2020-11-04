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

if [ -z "$1" ]
  then
    echo "Client id must be supplied"
fi

if [ -z "$2" ]
  then
    echo "Client secret must be supplied"
fi

PLAID_CLIENT_ID=$1 \
PLAID_SECRET=$2 \
PLAID_ENV='sandbox' \
PLAID_PRODUCTS='transactions' \
PLAID_COUNTRY_CODES='US' \
PLAID_ANDROID_PACKAGE_NAME='com.plaid.linksample' \
node index.js

# Go to http://localhost:8000
