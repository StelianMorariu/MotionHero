package com.stelianmorariu.motionhero.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Activity binding delegate, may be used since onCreate up to onDestroy (inclusive)
 */
inline fun <T : ViewBinding> AppCompatActivity.viewBinding(crossinline factory: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) {
        factory(layoutInflater)
    }

/**
 * Fragment binding delegate, may be used since onViewCreated up to onDestroyView (inclusive).
 *
 * This can be used ONLY if you use the new fragment constructor:
 *
 * ```Fragment(R.layout.fragment_layout)```
 *
 * or if you manually inflate the layout:
 *
 * ```
 *  override fun onCreateView( inflater: LayoutInflater,
 *                  container: ViewGroup?,
 *                  savedInstanceState: Bundle?): View? {
 *                      return inflater.inflate(R.layout.fragment_layout, container, false)
 *           }
 * ```
 */
fun <T : ViewBinding> Fragment.viewBinding(factory: (View) -> T): ReadOnlyProperty<Fragment, T> =
    object : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {
        private var binding: T? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T =
            binding ?: factory(requireView()).also {
                // if binding is accessed after Lifecycle is DESTROYED, create new instance, but don't cache it
                if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                    viewLifecycleOwner.lifecycle.addObserver(this)
                    binding = it
                }
            }

        override fun onDestroy(owner: LifecycleOwner) {
            binding = null
        }
    }

/**
 * DialogFragment binding delegate, may be used since onCreateView/onCreateDialog up to onDestroy (inclusive)
 */
inline fun <T : ViewBinding> DialogFragment.viewBinding(crossinline factory: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) {
        factory(layoutInflater)
    }

/*
 * For ViewHolders, use in onCreateViewHolder to pass a binding to the viewholder instead of the view eg:
 *
 *   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
 *       return Holder(parent.viewBinding(ListItemBinding::inflate))
 *   }
 *
 *   ...
 *
 *   class Holder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)
 */
inline fun <T : ViewBinding> ViewGroup.viewBinding(factory: (LayoutInflater, ViewGroup, Boolean) -> T) =
    factory(LayoutInflater.from(context), this, false)
