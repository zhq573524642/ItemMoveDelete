package zhq.com.itemmovedelete;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.ListView;

import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        listView.setAdapter(new MyAdapter(this,R.layout.item, Arrays.asList(Constant.NAMES)));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(nowOpenLayout!=null){
                    nowOpenLayout.close();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    ItemMoveDeleteLayout nowOpenLayout;//用来记录当前打开的layout
    class MyAdapter extends CommonAdapter<String>{

        public MyAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder viewHolder, String item, int position) {
            //控件设置数据
         viewHolder.setText(R.id.tv_name,item+position);

          ItemMoveDeleteLayout  itemMoveDeleteLayout = viewHolder.getView(R.id.itemMoveDeleteLayout);
          itemMoveDeleteLayout.setOnSwipeListener(new ItemMoveDeleteLayout.OnSwipeListener() {
              @Override
              public void onOpen(ItemMoveDeleteLayout openLayout) {
                    if(nowOpenLayout!=null&& nowOpenLayout != openLayout){
                        nowOpenLayout.close();
                    }
                    nowOpenLayout=openLayout;
              }

              @Override
              public void onClose(ItemMoveDeleteLayout closeLayout) {
                      if(nowOpenLayout==closeLayout){
                          nowOpenLayout=null;
                      }
              }
          });
        }
    }
}
