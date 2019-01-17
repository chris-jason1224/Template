
package com.cj.common.multitype;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.cj.common.multitype.Preconditions.checkNotNull;



public class MultiTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final String TAG = "MultiTypeAdapter";

  private @NonNull List<?> items;
  private @NonNull TypePool typePool;


  public MultiTypeAdapter() {
    this(Collections.emptyList());
  }

  /**
   * Constructs a MultiTypeAdapter with a items list.
   *
   * @param items the items list
   */
  public MultiTypeAdapter(@NonNull List<?> items) {
    this(items, new MultiTypePool());
  }


  /**
   * Constructs a MultiTypeAdapter with a items list and an initial capacity of TypePool.
   *
   * @param items the items list
   * @param initialCapacity the initial capacity of TypePool
   */
  public MultiTypeAdapter(@NonNull List<?> items, int initialCapacity) {
    this(items, new MultiTypePool(initialCapacity));
  }


  /**
   * Constructs a MultiTypeAdapter with a items list and a TypePool.
   *
   * @param items the items list
   * @param pool the type pool
   */
  public MultiTypeAdapter(@NonNull List<?> items, @NonNull TypePool pool) {
    checkNotNull(items);
    checkNotNull(pool);
    this.items = items;
    this.typePool = pool;
  }


  /**
   * Registers a type class and its item view binder. If you have registered the class,
   * it will override the original binder(s). Note that the method is non-thread-safe
   * so that you should not use it in concurrent operation.
   * <p>
   * Note that the method should not be called after
   * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
   * again.
   * </p>
   *
   * @param clazz the class of a item
   * @param binder the item view binder
   * @param <T> the item data type
   */
  public <T> void register(@NonNull Class<? extends T> clazz, @NonNull ItemViewBinder<T, ?> binder) {
    checkNotNull(clazz);
    checkNotNull(binder);
    checkAndRemoveAllTypesIfNeeded(clazz);
    register(clazz, binder, new DefaultLinker<T>());
  }


  <T> void register(
      @NonNull Class<? extends T> clazz,
      @NonNull ItemViewBinder<T, ?> binder,
      @NonNull Linker<T> linker) {
    typePool.register(clazz, binder, linker);
    binder.adapter = this;
  }


  /**
   * Registers a type class to multiple item view binders. If you have registered the
   * class, it will override the original binder(s). Note that the method is non-thread-safe
   * so that you should not use it in concurrent operation.
   * <p>
   * Note that the method should not be called after
   * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
   * again.
   * </p>
   *
   * @param clazz the class of a item
   * @param <T> the item data type
   * @return {@link OneToManyFlow} for setting the binders
   * @see #register(Class, ItemViewBinder)
   */
  @CheckResult
  public @NonNull <T> OneToManyFlow<T> register(@NonNull Class<? extends T> clazz) {
    checkNotNull(clazz);
    checkAndRemoveAllTypesIfNeeded(clazz);
    return new OneToManyBuilder<>(this, clazz);
  }


  /**
   * Registers all of the contents in the specified type pool. If you have registered a
   * class, it will override the original binder(s). Note that the method is non-thread-safe
   * so that you should not use it in concurrent operation.
   * <p>
   * Note that the method should not be called after
   * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
   * again.
   * </p>
   *
   * @param pool type pool containing contents to be added to this adapter inner pool
   * @see #register(Class, ItemViewBinder)
   * @see #register(Class)
   */
  public void registerAll(@NonNull final TypePool pool) {
    checkNotNull(pool);
    final int size = pool.size();
    for (int i = 0; i < size; i++) {
      registerWithoutChecking(
          pool.getClass(i),
          pool.getItemViewBinder(i),
          pool.getLinker(i)
      );
    }
  }


  /**
   * Sets and updates the items atomically and safely. It is recommended to use this method
   * to update the items with a new wrapper list or consider using {@link CopyOnWriteArrayList}.
   *
   * <p>Note: If you want to refresh the list views after setting items, you should
   * call {@link RecyclerView.Adapter#notifyDataSetChanged()} by yourself.</p>
   *
   * @param items the new items list
   * @since v2.4.1
   */
  public void setItems(@NonNull List<?> items) {
    checkNotNull(items);
    this.items = items;
  }


  public @NonNull List<?> getItems() {
    return items;
  }


  /**
   * Set the TypePool to hold the types and view binders.
   *
   * @param typePool the TypePool implementation
   */
  public void setTypePool(@NonNull TypePool typePool) {
    checkNotNull(typePool);
    this.typePool = typePool;
  }


  public @NonNull TypePool getTypePool() {
    return typePool;
  }


  @Override
  public final int getItemViewType(int position) {
    Object item = items.get(position);
    return indexInTypesOf(position, item);
  }


  @Override
  public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int indexViewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    ItemViewBinder<?, ?> binder = typePool.getItemViewBinder(indexViewType);
    return binder.onCreateViewHolder(inflater, parent);
  }


  @Override @Deprecated
  public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    onBindViewHolder(holder, position, Collections.emptyList());
  }


  @Override @SuppressWarnings("unchecked")
  public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
    Object item = items.get(position);
    ItemViewBinder binder = typePool.getItemViewBinder(holder.getItemViewType());
    binder.onBindViewHolder(holder, item, payloads);
  }


  @Override
  public final int getItemCount() {
    return items.size();
  }


  /**
   * Called to return the stable ID for the item, and passes the event to its associated binder.
   *
   * @param position Adapter position to query
   * @return the stable ID of the item at position
   * @see ItemViewBinder#getItemId(Object)
   * @see RecyclerView.Adapter#setHasStableIds(boolean)
   * @since v3.2.0
   */
  @Override @SuppressWarnings("unchecked")
  public final long getItemId(int position) {
    Object item = items.get(position);
    int itemViewType = getItemViewType(position);
    ItemViewBinder binder = typePool.getItemViewBinder(itemViewType);
    return binder.getItemId(item);
  }



  @Override @SuppressWarnings("unchecked")
  public final void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
    getRawBinderByViewHolder(holder).onViewRecycled(holder);
  }

  @Override @SuppressWarnings("unchecked")
  public final boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
    return getRawBinderByViewHolder(holder).onFailedToRecycleView(holder);
  }


  @Override @SuppressWarnings("unchecked")
  public final void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
    getRawBinderByViewHolder(holder).onViewAttachedToWindow(holder);
  }



  @Override @SuppressWarnings("unchecked")
  public final void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
    getRawBinderByViewHolder(holder).onViewDetachedFromWindow(holder);
  }


  private @NonNull ItemViewBinder getRawBinderByViewHolder(@NonNull RecyclerView.ViewHolder holder) {
    return typePool.getItemViewBinder(holder.getItemViewType());
  }


  int indexInTypesOf(int position, @NonNull Object item) throws BinderNotFoundException {
    int index = typePool.firstIndexOf(item.getClass());
    if (index != -1) {
      @SuppressWarnings("unchecked")
      Linker<Object> linker = (Linker<Object>) typePool.getLinker(index);
      return index + linker.index(position, item);
    }
    throw new BinderNotFoundException(item.getClass());
  }


  private void checkAndRemoveAllTypesIfNeeded(@NonNull Class<?> clazz) {
    if (typePool.unregister(clazz)) {
      Log.w(TAG, "You have registered the " + clazz.getSimpleName() + " type. " +
          "It will override the original binder(s).");
    }
  }


  /** A safe register method base on the TypePool's safety for TypePool. */
  @SuppressWarnings("unchecked")
  private void registerWithoutChecking(@NonNull Class clazz, @NonNull ItemViewBinder binder, @NonNull Linker linker) {
    checkAndRemoveAllTypesIfNeeded(clazz);
    register(clazz, binder, linker);
  }
}
