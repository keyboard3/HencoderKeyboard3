本作品为参加Hencoder投稿比赛之作
HenCoder「仿写酷界面」活动——征稿 http://hencoder.com/activity-mock-1

虽然本作品并未获奖，但本人还是会持续优化....，敬请关注！！！
### 即刻点赞
实现原理：隔离并复用图片动画和文字动画<br>
LikeView自定义的LinearLayout默认组合点赞文字动画效果

 - **LikeImageView**
  	- 点赞图片动画
    <br>**点赞**：
    <br>灰色的点赞图标变小至0.9倍-><br>转变成红色的图标并且半透明-><br>红色图标逐渐增长至正常的1.1倍 / 透明度在增长至正常时逐渐变成实体 -><br>最后变成正常大小的红色图标<br>
    - 点赞伴随动画光圈
    <br>0.6倍点赞图标大小的光圈、半透明-><br>0-50%动画完成度时半透明变成实体-><br>50%-100%动画完成度时实体又逐渐变成透明-><br>光圈半径逐渐增大至1.1倍<br>
    **取消点赞**：
    <br>红色图标变小至0.9倍且变成半透明 -><br>动画完成到一半时变成灰色的正常大小
  	- 闪光动画  
    点赞时 闪光图标在点赞图标顶部的某个位置，先由小到大直至正常大小
 - **LikeNumView** [简化了文字实现逻辑]
<br>点赞和取消赞动作导致的文字变化 转变成 原数字->新数字。点赞和取消赞时改动新数字的值(+1/-1)。将两个数字动转为字符串数组，从高位开始循环 如果数字相同就直接画数字，如果数字不同就开始绘制两个数字位移同时设置对应的透明渐变`

**使用代码**
```
<com.keyboard3.hencoderProduct.like.LikeView
    android:id="@+id/objectAnimatorView1"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_centerInParent="true"
    android:layout_margin="10dp"
    android:background="#2B2B2B"
    app:leftPadding="5dp"
    app:likeNum="9"
    app:likeSrc="@mipmap/ic_messages_like_selected"
    app:middlePadding="5dp"
    app:rightPadding="5dp"
    app:shiningSrc="@mipmap/ic_messages_like_selected_shining"
    app:unlikeSrc="@mipmap/ic_messages_like_unselected" />
```

![](images/like.gif)

### 薄荷健康尺

![](images/ruler.gif)

### 小米运动

![](images/miMove.gif)

### Fliboard 翻页效果

![](images/flipboard.gif)
