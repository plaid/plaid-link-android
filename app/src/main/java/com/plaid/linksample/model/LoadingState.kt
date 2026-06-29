/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.model

/** Preload status of a Link session; Ready means `onLoad` has fired. */
sealed interface LoadingState {
  data object Idle : LoadingState

  data object Loading : LoadingState

  data object Ready : LoadingState
}
