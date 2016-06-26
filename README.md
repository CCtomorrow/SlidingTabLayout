## SlidingTabLayout

一个Android ViewPager的TabLayout库.
SlidingTabLayout:参照[android-SlidingTabsBasic](https://github.com/googlesamples/android-SlidingTabsBasic)进行了修改.

#### Demo
![Preview](https://github.com/qingyongai/SlidingTabLayout/blob/master/sliding.gif)

#### Apk
[Demo Apk文件](https://github.com/qingyongai/SlidingTabLayout/blob/master/sample.apk)


#### Attributes

|name|format|description|
|:---:|:---:|:---:|
| tab_title_offset | dimension | tab数量较多的时候，设置最左边的tab的一个偏移
| tab_distribut_col_evenly | boolean | tab数量较少的时候平分列，默认false
| tab_view_pager_smooth_scroll | boolean | ViewPager平滑的滑动，默认true
| tab_text_type_face | enum | Tab TextView Typeface
| tab_text_size | dimension | Tab TextView的文字大小
| tab_text_color | dimension | Tab TextView的文字颜色
| tab_top_border_color | color | top Bordor颜色
| tab_top_border_height | dimension | top Bordor高度
| tab_bottom_border_color | color | bottom Bordor颜色
| tab_bottom_border_height | dimension | bottom Bordor高度
| tab_top_indicator_color | color | Top Indicator颜色 
| tab_top_indicator_height | dimension | Top Indicator高度
| tab_indicator_padding | dimension | Indicator 不想全部的占据整个View，给个padding
| tab_bottom_indicator_color | color | Bottom Indicator颜色
| tab_bottom_indicator_height | dimension | Bottom Indicator高度
| tab_text_indicator_color | color | Text Indicator颜色
| tab_text_indicator_stroke | dimension | Text Indicator高度
| tab_text_indicator_padding | dimension | Text 可能需要超出Text一点距离
| tab_divider_color | color | Divider颜色
| tab_divider_stroke | dimension | Divider宽度
| tab_divider_heiht_ratio | float | Divider高度占总高度的比例范围0-1
| tab_text_padding | dimension | Tab TextView的Padding
| tab_text_padding_left | dimension | Tab TextView的Padding
| tab_text_padding_top | dimension | Tab TextView的Padding
| tab_text_padding_right | dimension | Tab TextView的Padding
| tab_text_padding_bottom | dimension | Tab TextView的Padding

#### Dependence
android:support-v4 ViewPager

#### Thanks
*   [android-SlidingTabsBasic](https://github.com/googlesamples/android-SlidingTabsBasic)
