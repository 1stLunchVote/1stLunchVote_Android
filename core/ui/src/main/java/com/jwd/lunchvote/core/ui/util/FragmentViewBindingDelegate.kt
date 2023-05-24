package com.jwd.lunchvote.core.ui.util

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<VB: ViewDataBinding>(
    private val fragment: Fragment,
    private val factory: (LayoutInflater) -> VB
): ReadOnlyProperty<Fragment, VB> {
    private var binding: VB? = null

    private val fragmentObserver = object : DefaultLifecycleObserver {
        private val viewLifecycleOwnerObserver = Observer<LifecycleOwner?> { viewLifecycleOwner ->
            viewLifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    binding = null
                }
            })
        }

        override fun onCreate(owner: LifecycleOwner) {
            fragment.viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerObserver)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            fragment.viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerObserver)
            fragment.lifecycle.removeObserver(this)
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        binding?.let { return it }

        fragment.lifecycle.addObserver(fragmentObserver)

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED).not()) {
            throw IllegalStateException("binding should not be null")
        }

        return factory(thisRef.layoutInflater).also {
            binding = it
        }
    }
}

inline fun <reified VB : ViewDataBinding> Fragment.viewBindings(
    noinline factory: (LayoutInflater) -> VB
) = FragmentViewBindingDelegate(this, factory)