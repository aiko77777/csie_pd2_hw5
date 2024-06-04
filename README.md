2024/06/02:原本在HW4，是把總文本每五行分成一個section，再存成一個trie，再存在arraylist<Trie> trie_list，但好像沒辦法把trie_list序列化，只好先把分好的文本setction序列化。indexer意義不明。
2024/06/03:輸出結果正確，但執行時間過長。
2024/06/04:測資中同樣的元素例如 a AND a AND a AND a AND.....算出的tf_idf值不會一樣，但理當上應該為相同，經過實測，tf計算需要修正，也可以從測資上重複元素多的方面下手解決速度問題。
