//
//  hashtable.h
//  mastermind
//
//  semi-generic hash table class using chained hashing
//
//  all methods must be implemented here, due to using template

#ifndef __mastermind__hashtable__
#define __mastermind__hashtable__

#include <functional>   // for std::hash
#include <exception>    // for when a key is null
#include <iostream>
#include <string>
using std::cout;
using std::endl;

#define TABLE_SIZE    10

//template<class K, class V>
//void foo(const K& k, const V& v)
//{
//    cout << "foo " << k << ", " << v << endl;
//}

// hashtable class
template <class K, class V> class hashtable
{
public:
    // ------------------------------------------------
    // constructor
    hashtable()
    {
        table = new node*[TABLE_SIZE]();
//        for (int i = 0; i < TABLE_SIZE; i++)
//            table[i] = NULL;
    }
    
    // ------------------------------------------------
    // contains key
    bool containsKey(const K &key)
    {
        int index = hash(key);
        if (table[index] == NULL)
            return false;
        
        node* n = table[index];
        do {
            if (n->key == key)
                return true;
        } while ((n = n->link) != NULL);
        
        return false;
    }
    
    // ------------------------------------------------
    // get
    // stores value in value parameter
    // returns true if this key exists, false if not
    bool get(const K &key, V &value)
    {
        // hash the key and check if present in table
        int index = hash(key);
        if (table[index] == NULL)
            return false;
        
        // if it is present, find it in the linked list
        node* n = table[index];
        do {
            if (n->key == key) {
                value = n->value;
                return true;
            }
        } while ((n = n->link) != NULL);
        
        // otherwise, it doesn't exist in the list
        return false;
    }
    
    // ------------------------------------------------
    // put
    // puts this key,value pair in the hashtable
    // returns true if this key already existed
    bool put(const K &key, const V &value)
    {
        node* cursor;   // for traversing list
        
        // check if element already exists in table
        if (containsKey(key)) {
            int index = hash(key);
            cursor = table[index];
            // traverse until key found
            // wont hit end of list, because key DOES exist
            while (cursor->key != key && (cursor = cursor->link) != NULL);
            cursor->value = value;
            return true;
        }
        // if key doesnt already exist:
        else {
            // add a new node at the front of the list
            int index = hash(key);
            cursor = new node(key, value, table[index]);
            table[index] = cursor;
            return false;
        }
    }
    
    // ------------------------------------------------
    // remove
    // stores removed value in value parameter
    // returns if this key exists, false if not
    bool remove(const K &key, V &value)
    {
        int index = hash(key);
        if (table[index] == NULL)
            return false;
        
        node* prev = NULL;
        node* n = table[index];
        
        // find the key in the list
        while (n != NULL) {
            if (n->key == key) {
                // if found, set value and remove node
                value = n->value;
                if (prev == NULL)
                    table[index] = n->link;
                else
                    prev->link = n->link;
                return true;
            }
            prev = n;
            n = n->link;
        }
        return false;
    }
    
    // ------------------------------------------------
    // toString() equivalent method
    void print()
    {
        node* n;
        cout << "\n------------------------------------" << endl;
        for (int i = 0; i < TABLE_SIZE; i++) {
            cout << i << " : " << endl;
            n = table[i];
            while (n != NULL) {
                cout << "\t" << n->key << "\t" << n->value << endl;
                n = n->link;
            }
        }
        cout << "------------------------------------\n" << endl;
    }

    
private:
    
    // linked list node for chained hashing
    class node
    {
    public:
        node()
        {
            key = 0;
            value = 0;
            link = NULL;
        }
        node(const K &k, const V &v, node* l)
        {
            key = k;
            value = v;
            link = l;
        }
        K key;
        V value;
        node* link;
    };
    
    node** table;
    std::hash<K> hasher;    // for hashing key values
    
    // -----
    // hash
    int hash(const K &key)
    {
        int h = (int)hasher(key);
        return h % TABLE_SIZE;
    }
};


#endif /* defined(__mastermind__hashtable__) */
