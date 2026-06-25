/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample.model

/** Preload status of a Link session: idle, loading, or ready ([onLoad] fired). */
sealed interface LoadingState {
  data object Idle : LoadingState

  data object Loading : LoadingState

  data object Ready : LoadingState
}
