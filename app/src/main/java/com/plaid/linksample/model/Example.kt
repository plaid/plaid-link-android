/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.model

/** The Link flows this sample demonstrates, one per detail screen. */
enum class Example(
  val title: String,
  val description: String,
) {
  STANDARD(
    title = "Standard Link",
    description = "Create a Link session and open it with the OpenPlaidLink launcher.",
  ),
  HEADLESS(
    title = "Headless",
    description = "Run an external-browser OAuth flow with no in-app WebView.",
  ),
  LAYER(
    title = "Layer",
    description = "Submit a phone number, then open Layer once it reports it is ready.",
  ),
  EMBEDDED(
    title = "Embedded",
    description = "Host the inline institution-search view and continue into Link on selection.",
  ),
}
