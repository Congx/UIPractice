### 记录下recyclerView的一些问题

###### ItemDecoration
* item的装饰，可用于分割线、分组等等效果的实现
* 主要有onDraw、getItemOffsets两个方法，recyclerView绘制过程中getItemOffsets在每个item
绘制的时候都会去回调,应该是发生在onLayout阶段，并且第一个参数outRect可以为每个itemview设置偏移（margin、padding）
整个item,的组成为：itemview（adapter绑定的item），和外面的装饰。onDraw方法每次滑动、或者
整个recyclerview重绘的时候调用，第一个参数canvas，为recyclerview的cavans,这里就操作空间
很大了，可以发挥很多想象。