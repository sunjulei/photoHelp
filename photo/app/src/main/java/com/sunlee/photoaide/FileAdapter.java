package com.sunlee.photoaide;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义适配器
 */
public class FileAdapter extends BaseAdapter {

    private int selectedPosition = 100000;

    /**
     * 上下文
     */
    Context context;

    /**
     * 数据
     */
    File[] files;
    /**
     * 判断是否选中文件
     */
    List<IsSelectedDirestory> list = new ArrayList<>();

    // 或者列表（对象）
    ArrayList<File> fileList;


    // 加载、解析 XML（系统服务 XmlPullParser xpp）
    LayoutInflater layoutInflater;


    ViewHolder holder;

    /**
     * 文件适配器：构造方法
     *
     * @param context 上下文
     * @param files   数据
     */
    public FileAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
        resetData(files.length);
        // 获得服务实例
        layoutInflater = LayoutInflater.from(context);

//        layoutInflater = (LayoutInflater) context.getSystemService(
//                Context.LAYOUT_INFLATER_SERVICE);
    }


    /**
     * 获得数据总数
     *
     * @return
     */
    @Override
    public int getCount() {
        return files.length;
    }

    /**
     * 获得特定位置的数据
     *
     * @param i 位置
     * @return
     */
    @Override
    public File getItem(int i) {
        return files[i];
    }

    /**
     * 获得特定位置数据的 ID
     *
     * @param i 位置
     * @return 数据在数据库中 PK(id)
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * 创建视图项
     *
     * @param i           位置
     * @param convertView 可复用的视图，可能 null (需要新建)
     * @param viewGroup   适配器视图
     * @return
     */
    @Override
    public View getView(
            int i,
            View convertView,
            ViewGroup viewGroup) {


        if (convertView == null) {
            // 没有可复用，需要创建
            // 开销很大 加载文件、XML 解析 控件和布局的
            convertView = layoutInflater.inflate(R.layout.file_item, null);

            // 每个视图项需要一个 viewHolder
            // 构造ViewHolder把View convertView传给它
            holder = new ViewHolder(convertView);

            // 视图项关联了它的视图结构
            convertView.setTag(holder);
        } else {
            // 有复用视图项，不创建，并直接获得结构
            holder = (ViewHolder) convertView.getTag();
        }

        Log.d("viewHolder", holder.id + " : " + i);

        // 加载数据
        holder.bindData(files[i], i);

        return convertView;
    }

    static int counter = 1;

    /**
     * ViewHolder 模式
     */
    class ViewHolder {

        ImageView icon;
        TextView title;
        TextView info;
        ImageButton action;
        int id;


        /**
         * 构造方法
         * 得到View v   通过findViewById获得布局引用
         *
         * @param v
         */
        public ViewHolder(View v) {
            icon = (ImageView) v.findViewById(R.id.imageView_icon);
            title = (TextView) v.findViewById(R.id.textView_name);
            info = (TextView) v.findViewById(R.id.textView_info);
            action = (ImageButton) v.findViewById(R.id.imageButton_action);
            id = counter++;
        }


        public void bindData(File file, final int position) {
            if (file.isDirectory()) {
                icon.setImageResource(R.drawable.item_1);
            } else {
                String fileName = file.getAbsolutePath();
                if (fileName.length() > 0) {
                    fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
                }
                switch (fileName) {
                    case "txt":
                        icon.setImageResource(R.drawable.item_3);
                        break;
                    case "mp4":
                        icon.setImageResource(R.drawable.item_2);
                        break;
                    case "3pg":
                        icon.setImageResource(R.drawable.item_2);
                        break;
                    case "doc":
                        icon.setImageResource(R.drawable.item_word);
                        break;
                    case "JPG":
                        icon.setImageResource(R.drawable.item_2);
                        break;
                    case "jepg":
                        icon.setImageResource(R.drawable.item_2);
                        break;
                    case "apk":
                        icon.setImageResource(R.drawable.item_2);
                        break;
                    default:
                        icon.setImageResource(R.drawable.item_2);
                        break;

                }
            }

            title.setText(file.getName());
            // 1,234,567file.la
            if (file.isFile()) {
                action.setVisibility(View.GONE);
                String fileSiz = FormetFileSize(file.length());
                info.setText(CommonUtils.getFirmattedTime(file.lastModified()) + "    " + "大小：" + fileSiz);
            } else {
                action.setVisibility(View.VISIBLE);
                String fileAndDirectoryNumber = getNumber(file);
                info.setText(fileAndDirectoryNumber);
            }


            /**
             * 选择文件夹
             */
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isSelected()) {
                            list.get(i).setSelected(false);
                        }
                    }
                    list.get(position).setSelected(true);
                    notifyDataSetChanged();
                }
            });

            if (list.get(position).isSelected()) {
                selectedPosition = position;
                action.setImageResource(R.drawable.item_tick2);
            } else {
                action.setImageResource(R.drawable.item_tick);
            }


        }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    /**
     * 更新数据
     *
     * @param files
     */
    public void setAllData(File[] files) {
        resetData(files.length);
        this.files = files;
        selectedPosition = 100000;
        notifyDataSetChanged();
    }

    /**
     * 判断有几个文件几个文件夹
     */
    File[] mFileList;
    private int fileSize;
    private int directorySize;

    public String getNumber(File file) {
        fileSize = 0;
        directorySize = 0;
        File[] fileArray = file.listFiles();
        if (fileArray.length > 0) {
            for (int i = 0; i < fileArray.length; i++) {
                File fil = fileArray[i];
                if (fil.isDirectory()) {
                    directorySize++;
                } else {
                    fileSize++;
                }
            }
        }
        return "文件: " + fileSize + "," + "   文件夹:    " + directorySize;
    }

    /**
     * 重置选中的数据
     *
     * @param size
     */
    private void resetData(int size) {
        list.clear();
        for (int i = 0; i < size; i++) {
            IsSelectedDirestory entity = new IsSelectedDirestory();
            entity.setSelected(false);
            list.add(entity);
        }
    }

    /**
     * 获取文件夹路径
     *
     * @return
     */
    public String getSelectedDirestoryPath() {
        if (selectedPosition == 100000) {

        } else {
            return getItem(selectedPosition).getAbsolutePath();
        }
        return "null";
    }


}
