/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cj.common.multitype;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.List;

/***
 * @author drakeet
 */
public abstract class ItemViewBinder<T, VH extends RecyclerView.ViewHolder> {

  /* internal */ MultiTypeAdapter adapter;


  protected abstract @NonNull VH onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);


  protected abstract void onBindViewHolder(@NonNull VH holder, @NonNull T item);

  protected void onBindViewHolder(@NonNull VH holder, @NonNull T item, @NonNull List<Object> payloads) {
    onBindViewHolder(holder, item);
  }

  protected final int getPosition(@NonNull final RecyclerView.ViewHolder holder) {
    return holder.getAdapterPosition();
  }


  /**
   * Get the {@link MultiTypeAdapter} for sending notifications or getting item count, etc.
   * <p>
   * Note that if you need to change the item's parent items, you could call this method
   * to get the {@link MultiTypeAdapter}, and call {@link MultiTypeAdapter#getItems()} to get
   * a list that can not be added any new item, so that you should copy the items and just use
   * {@link MultiTypeAdapter#setItems(List)} to replace the original items list and update the
   * views.
   * </p>
   *
   * @return The MultiTypeAdapter this item is currently associated with.
   * @since v2.3.4
   */
  protected final @NonNull MultiTypeAdapter getAdapter() {
    if (adapter == null) {
      throw new IllegalStateException("ItemViewBinder " + this + " not attached to MultiTypeAdapter. " +
          "You should not call the method before registering the binder.");
    }
    return adapter;
  }


  /**
   * Return the stable ID for the <code>item</code>. If {@link RecyclerView.Adapter#hasStableIds()}
   * would return false this method should return {@link RecyclerView#NO_ID}. The default
   * implementation of this method returns {@link RecyclerView#NO_ID}.
   *
   * @param item The item within the MultiTypeAdapter's items data set to query
   * @return the stable ID of the item
   * @see RecyclerView.Adapter#setHasStableIds(boolean)
   * @since v3.2.0
   */
  protected long getItemId(@NonNull T item) {
    return RecyclerView.NO_ID;
  }


  protected void onViewRecycled(@NonNull VH holder) {}


  protected boolean onFailedToRecycleView(@NonNull VH holder) {
    return false;
  }


  /**
   * Called when a view created by this {@link ItemViewBinder} has been attached to a window.
   *
   * <p>This can be used as a reasonable signal that the view is about to be seen
   * by the user. If the {@link ItemViewBinder} previously freed any resources in
   * {@link #onViewDetachedFromWindow(RecyclerView.ViewHolder) onViewDetachedFromWindow}
   * those resources should be restored here.</p>
   *
   * @param holder Holder of the view being attached
   * @since v3.1.0
   */
  protected void onViewAttachedToWindow(@NonNull VH holder) {}


  /**
   * Called when a view created by this {@link ItemViewBinder} has been detached from its
   * window.
   *
   * <p>Becoming detached from the window is not necessarily a permanent condition;
   * the consumer of an Adapter's views may choose to cache views offscreen while they
   * are not visible, attaching and detaching them as appropriate.</p>
   *
   * @param holder Holder of the view being detached
   * @since v3.1.0
   */
  protected void onViewDetachedFromWindow(@NonNull VH holder) {}
}
