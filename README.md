# GuaGuaKa
一个天猫，京东“刮刮卡”效果的自定义View

### GuaGuaKa是一个用自定义View实现刮刮卡效果的控件，支持自定义属性包括：自定义背景颜色，自定义刮卡图片，自定义中奖文字，自定义中奖文字大小，自定义中奖文字颜色，自定义刮卡时的画笔宽度，效果如下：

![](https://github.com/sheng-xiaoya/GuaGuaKa/blob/master/screen/guaguaka.gif)

##自定义属性：
### GuaGuaKa支持的属性
| attr          | description           | format  |
| ------------- |:-------------:| -----:|
| guaguaka_textcolor     | 刮刮卡文字颜色         | color |
| guaguaka_textsize      | 刮刮卡文字的大小                     |   dimension |
| guaguaka_color         | 刮刮卡背景颜色            |    color |
| guaguaka_img           | 刮刮卡图片             |    reference |
| guaguaka_pen_size      | 刮卡时画笔粗细           |    dimension |

### GuaGuaK支持动态设置刮刮卡的文字，默认为“谢谢惠顾”，支持刮奖完成时的监听回调，示例代码如下：

```
        mGuaGuaKa = (GuaGuaKa) findViewById(R.id.guaguaka);
        //设置中奖信息文字
        mGuaGuaKa.setText("￥500,000,000");
        //刮奖完成时回调接口
        mGuaGuaKa.setCompleteListener(new GuaGuaKa.OnGuaGuaKaCompleteListener() {
            @Override
            public void onComplete() {
                Toast.makeText(getApplicationContext(),"刮奖完成了",Toast.LENGTH_SHORT).show();
            }
        });
```
