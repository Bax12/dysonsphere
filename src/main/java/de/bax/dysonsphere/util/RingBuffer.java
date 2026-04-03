package de.bax.dysonsphere.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

//it's not perfect, but good enough.
@SuppressWarnings("unchecked")
public class RingBuffer<T> implements Collection<T> {
    
    public final int capacity;
    protected int start = 0;
    protected int end = 0;
    protected Object[] data;

    public RingBuffer(int capacity){
        this.capacity = capacity+1; // to accommodate the one needed empty slot.
        data = new Object[this.capacity];
    }

    public void push(T item){
        data[end] = item;
        end = ++end % capacity;
        if(start == end){
            start = ++start % capacity;
        }
    }

    public T pop(){
        T ret = peek();
        if(start != end){
            start = ++start % capacity;
        }
        return ret;
    }

    
    public T peek(){
        return (T) data[start];
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return (start + index) % capacity != end;
            }

            @Override
            public T next() {
                return (T) data[(start + index++) % capacity];
            }
            
        };
    }

    @Override
    public boolean add(T arg0) {
        push(arg0);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.forEach((e) -> {
            push(e);
        });
        return true;
    }

    @Override
    public void clear() {
        start = end = 0;
    }

    @Override
    public boolean contains(Object o) {
        for(int i = 0; i < capacity; i++){
            if(data[i] != null && data[i].equals(o)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object e : c){
            if(!contains(e)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return start == end;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public int size() {
        return (end - start) % capacity;
    }

    @Override
    public Object[] toArray() {
        //will copy null entries. Should probably not?
        return Arrays.copyOf(data, capacity);
    }

    @Override
    public <E> E[] toArray(E[] arg0) {
        //todo...
        return (E[]) Arrays.copyOf(data, capacity);
    }



}
