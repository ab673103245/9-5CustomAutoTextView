package qianfeng.a9_5customautotextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoCompleteTv = ((AutoCompleteTextView) findViewById(R.id.autoCompleteTv));

        List<Book> list = new ArrayList<>();
        list.add(new Book(R.mipmap.ic_launcher, "三国演义", "罗贯中"));
        list.add(new Book(R.mipmap.ic_launcher, "西游记", "吴承恩"));
        list.add(new Book(R.mipmap.ic_launcher, "水浒传", "施耐庵"));
        list.add(new Book(R.mipmap.ic_launcher, "红楼梦", "曹雪芹"));

        // 由于这个AutoCompleteTextView的Adapter的特殊性问题，所以在getView方法中调用notifyDataSetChange时，list的数据一发生改变就通知它重新显示item的内容。
        autoCompleteTv.setAdapter(new MyAdapter(this,list));

    }
}
