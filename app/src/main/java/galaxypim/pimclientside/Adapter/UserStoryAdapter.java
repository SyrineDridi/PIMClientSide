package galaxypim.pimclientside.Adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;


import java.util.ArrayList;


import galaxypim.pimclientside.Entities.UserStory;
import galaxypim.pimclientside.R;

/**
 * Created by Syrine on 05/04/2017.
 */
public class UserStoryAdapter extends RecyclerView.Adapter<UserStoryAdapter.MyViewHolder2> {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<UserStory> users;

    public UserStoryAdapter(Context context, ArrayList<UserStory> users) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.users = users;
    }

    @Override
    public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = inflater.inflate(R.layout.list_user_item, parent, false);
        MyViewHolder2 holder = new MyViewHolder2(view);
        return holder;

    }


    @Override
    public void onBindViewHolder(MyViewHolder2 holder, int position) {

        final int itemType = getItemViewType(position);
        UserStory current = users.get(position);
        holder.setData(current, position);
        holder.setListeners();

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    class MyViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName, tvAvancement, tvDesc;
        ImageView img;
        int position;
        UserStory current;
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color1 = generator.getRandomColor();
        public MyViewHolder2(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            img=(ImageView) itemView.findViewById(R.id.imageView4) ;
            tvAvancement = (TextView) itemView.findViewById(R.id.tvEtat);
            tvDesc = (TextView) itemView.findViewById(R.id.tvHours);
        }

        public void setData(UserStory current, int position) {
            this.tvName.setText(current.getNom_projet());
            this.tvAvancement.setText(current.getAvancement());
            this.tvDesc.setText(current.getDesc());
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(current.getDesc().substring(0,1).toUpperCase(), color1);
            img.setImageDrawable(drawable);
            this.position = position;
            this.current = current;
        }

        public void setListeners() {
            //imgDelete.setOnClickListener(MyViewHolder2.this);
            //imgAdd.setOnClickListener(MyViewHolder2.this);
        }

        @Override
        public void onClick(View v) {
          /*  switch (v.getId()) {
                case R.id.img_row_delete:
                     removeItem(position);
                    break;

                case R.id.img_row_add:
                    //  addItem(position, current);
                    break;
            }*/
        }

    }
}
