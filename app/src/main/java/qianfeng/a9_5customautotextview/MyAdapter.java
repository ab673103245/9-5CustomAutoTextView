package qianfeng.a9_5customautotextview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/10/28 0028.
 */
public class MyAdapter extends BaseAdapter implements Filterable {
    // 注意这个MyAdapter是适配AutoCompleteTextView的数据的，所以里面的list一改变就调用notifyDataSetChange，来通过重新调用getView来重新显示item

    private Context context;
    private LayoutInflater inflater;
    // 未经过过滤的书 (待会有过滤结果也是重新写进去这里)
    private List<Book> list;
    // 经过过滤的书(通过getFilter方法实现的结果)
    private List<Book> filterBooks;
    // 另外两个集合用来存放一本书的 全拼 和 首字母拼音
    //书名全拼  三国演义[sanguoyanyi]
    //水浒传[shuihuzhuan,shuihuchuan,shuixuzhuan,shuixuchuan]
    private List<Set<String>> bookNamePinYin; // 存放一本书的全拼的集合

    private List<Set<String>> bookNamePY;// 存放一本书的首字母拼音的集合

    private PinYin4j pinYin4j;

    private boolean isAdd = false; // 为了保证满足两种条件的拼音不被重复添加进list集合中而定义的boolean变量。

    public MyAdapter(Context context, List<Book> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        // 在构造方法里面初始化什么？
        pinYin4j = new PinYin4j();

        bookNamePinYin = new ArrayList<>();
        bookNamePY = new ArrayList<>();

        for (Book book : list) {
            // 遍历传进来的这个list集合，把里面的中文，全部转化为全拼或者拼音首字母，传进去两个集合中,思考为什么要这么做？待会的过滤要用到？
            bookNamePinYin.add(pinYin4j.getAllPinyin(book.getBookName()));
            bookNamePY.add(pinYin4j.getPinyin(book.getBookName()));// 现在完成这两步后，这两个集合都有传进来的list的全部拼音的集合(包括多音字)和首字母拼音的集合

        }


    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item, parent, false);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            holder.authorName = (TextView) convertView.findViewById(R.id.author);
            holder.bookName = (TextView) convertView.findViewById(R.id.bookname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 这个list是重新调用notifyDataSetChange时，经过过滤后的list？
        Book book = list.get(position);
        holder.iv.setImageResource(book.getBookImg());
        holder.bookName.setText(book.getBookName());
        holder.authorName.setText(book.getBookAuthor());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new MyFilter(); // new一个文件过滤器类对象
    }

    // 来一个类，专门用来过滤用户搜索的关键字所对应的结果集，存放在原来传进来的list集合中，只不过这次要调用notifyDataSetChange使其重新调用getView方法
    class MyFilter extends Filter { // 继承自抽象类Filter

        //根据用户输入的内容进行过滤
        //参数表示用户在AutoCompleteTextView中输入的过滤文本
        // 这个方法是每当用户输入的文本发生改变的时候，就会调用的，那么记录结果的这个filterBooks肯定不能被new多次，只能被new一次
        @Override
        protected FilterResults performFiltering(CharSequence constraint) { // 这个constraint是用户输入的字符串(用户输入的过滤文本)

            FilterResults results = new FilterResults();

            if (filterBooks == null) {
                // new出来的这个要过滤的集合，使用list作为参数的，也就是这个是复制从外面传进来的list的所有数据来构造自身的集合！
                filterBooks = new ArrayList<>(list); // filterBooks是new出来用于被过滤的集合!!! 注意里面传了个参数list!!
            }

            if (constraint == null && constraint.toString().length() == 0) { // 如果用户没有输入文本
                results.count = filterBooks.size();
                results.values = filterBooks; // 把这个ArrayList结果集返回。
            }else  // 如果用户有输入文本
            {
                List<Book> bookList = new ArrayList<>();
                // 拿到过滤文本后怎么处理？一是判断是否中文，二是处理拼音。

                String lowerCase = constraint.toString().toLowerCase(); // 中文全部转化为小写之后还是中文

                for (int i = 0; i < filterBooks.size(); i++) {
                    isAdd = false; // 每次循环一开始，这个i所对应的元素都没有被添加进bookList集合中
                    Book book = filterBooks.get(i);
                    //假设用户输入了中文查询条件, filterBooks就是由list里面的数据原封不动的构造出来的，里面当然存储书名是用中文的
                    if(book.getBookName().contains(lowerCase)) //假设用户输入了中文查询条件,
                    {
                        bookList.add(book);
                    }else  // 用户输入的是拼音的情况,分两种，全拼和拼音首字母
                    {
                        if(!isAdd) // 为了保证满足两种条件的拼音不被重复添加进list集合中而定义的boolean变量。
                        {
                            // 用户输入了拼音查询的条件
                            // 姓名全拼查询
                            Set<String> strings = bookNamePinYin.get(i);
                            Iterator<String> iterator = strings.iterator();
                            while(iterator.hasNext())
                            {
                                String next = iterator.next();
                                if(next.contains(lowerCase))
                                {
                                    bookList.add(book);
                                    isAdd = true;
                                    break;
                                }
                            }
                        }

                        if(!isAdd)
                        {
                            // 姓名简拼查询
                            Set<String> strings = bookNamePY.get(i);
                            Iterator<String> iterator = strings.iterator();
                            while(iterator.hasNext())
                            {
                                String next = iterator.next();
                                if(next.contains(lowerCase))
                                {
                                    bookList.add(book);
                                    isAdd = true;
                                    break;
                                }
                            }
                        }
                    }


                }

                results.count = bookList.size();
                results.values = bookList;

            }

            return results;
        }

        // 发布过滤结果
        // 有过滤结果时会传递这个FilterResults集合过来
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Book> values = (List<Book>) results.values;
            // 思考一下，为什么用户没有输入文本的时候，原本在外面onCreate的数据就被保护下来了呢？
            if(values != null && values.size() > 0)
            {
                list.removeAll(list);
                list.addAll(values);
                notifyDataSetChanged();// 当数据源list过滤成功后，就重新刷新一次数据,重新调用getView()
                // 这时候，它显示的list就发生了改变了，是过滤后的list集合的内容
            }

        }
    }


    class ViewHolder {
        ImageView iv;
        TextView bookName, authorName;
    }
}
