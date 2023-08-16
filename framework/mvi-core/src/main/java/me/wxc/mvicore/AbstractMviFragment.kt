package me.wxc.mvicore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.*

abstract class AbstractMviFragment<
  I : MviIntent,
  S : MviViewState,
  E : MviSingleEvent,
  VM : MviViewModel<I, S, E>,
  >(@LayoutRes contentLayoutId: Int) :
  Fragment(contentLayoutId), MviView<I, S, E> {
  protected abstract val vm: VM
  protected abstract val setupView: View.() -> Unit

  @CallSuper
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    onPreCreateView(inflater, container, savedInstanceState)
    val view = rootView().apply {
      setupView.invoke(this)
    }
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bindVM()
  }

  protected abstract fun onPreCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  )

  private fun bindVM() {
    // observe view model
    vm.viewState
      .collectInViewLifecycle(this) { render(it) }

    // observe single event
    vm.singleEvent
      .collectInViewLifecycle(this) { handleSingleEvent(it) }

    // pass view intent to view model
    viewIntents()
      .onEach { vm.processIntent(it) }
      .launchIn(lifecycleScope)
  }

  protected abstract fun rootView(): View
}
