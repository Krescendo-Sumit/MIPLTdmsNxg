package com.mahyco.feedbacklib.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahyco.feedbacklib.R;
import com.mahyco.feedbacklib.model.ModelQNS;

import java.util.ArrayList;

public class QuestionAnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ModelQNS> modelQNSArrayList = new ArrayList<ModelQNS>();
    private Context mContext;
    private QuestionAnswerListener listener;
    private AlertDialog dialog;

    private static final int LAYOUT_STAR = 101;
    private static final int LAYOUT_COMMENT = 102;
    private static final int LAYOUT_MODULE = 103;
    private static final int LAYOUT_HEADING = 104;

    public QuestionAnswerAdapter(Context mContext, ArrayList<ModelQNS> modelQNSArrayList, AlertDialog alertAdd) {
        this.mContext = mContext;
        this.modelQNSArrayList = modelQNSArrayList;
        this.dialog = alertAdd;
    }

    public void setListener(QuestionAnswerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_question_answer, parent, false);
        QuestionAnswerViewHolder viewHolder = new QuestionAnswerViewHolder(view);
        return viewHolder;*/

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == LAYOUT_STAR) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_question_answer, parent, false);
            viewHolder = new QuestionAnswerViewHolder(view);
        } else if (viewType == LAYOUT_MODULE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_module, parent, false);
            viewHolder = new ModuleViewHolder(view);
        } else if (viewType == LAYOUT_COMMENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_comment, parent, false);
            viewHolder = new CommentViewHolder(view);
        } else if (viewType == LAYOUT_HEADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_heading, parent, false);
            viewHolder = new HeadingViewHolder(view);
        }

        return viewHolder;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (modelQNSArrayList.get(position).getqType().equalsIgnoreCase("Rating")) {
            final QuestionAnswerViewHolder qsVH = (QuestionAnswerViewHolder) holder;

            qsVH.bindQuesAns(modelQNSArrayList.get(position));

            /*if (!TextUtils.isEmpty((modelQNSArrayList.get(position).getRating()))) {
                String val = modelQNSArrayList.get(position).getRating();
                val.replace(".0","");
                Float aFloat = Float.valueOf(val);
                qsVH.rbRating.setRating(aFloat);
            }*/

            qsVH.rbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    //tvRTOne.setText(String.format("%2.1f", rating)); //1 decimal
                    int val = Math.round(rating);
                    qsVH.tvRating.setText("" + val);
                    modelQNSArrayList.get(position).setRating("" + val);
                    if (listener != null) {
                        listener.onListChange(modelQNSArrayList);
                    }
                }
            });
        } else if (modelQNSArrayList.get(position).getqType().equalsIgnoreCase("Module")) {
            final ModuleViewHolder mdVH = (ModuleViewHolder) holder;
            mdVH.bindModule(modelQNSArrayList.get(position));
            mdVH.tvModuleTitle.setText(modelQNSArrayList.get(position).getQuestion());
            mdVH.edModule.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });
            mdVH.edModule.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mdVH.edModule.hasFocus()) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_SCROLL:
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                return true;
                        }
                    }
                    return false;
                }
            });
            mdVH.edModule.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length()!=0){
                        modelQNSArrayList.get(position).setRating("" + s.toString());
                    }
                }
            });

        } else if (modelQNSArrayList.get(position).getqType().equalsIgnoreCase("Comment")) {
            final CommentViewHolder cmVH = (CommentViewHolder) holder;
            cmVH.bindComment(modelQNSArrayList.get(position));
            cmVH.tvComment.setText(modelQNSArrayList.get(position).getQuestion());

            cmVH.edComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });

            cmVH.edComment.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (cmVH.edComment.hasFocus()) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_SCROLL:
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                return true;
                        }
                    }
                    return false;
                }
            });

            cmVH.edComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length()!=0){
                        modelQNSArrayList.get(position).setRating("" + s.toString());
                    }
                }
            });
        }else if (modelQNSArrayList.get(position).getqType().equalsIgnoreCase("Heading")){
            final HeadingViewHolder headVH = (HeadingViewHolder) holder;
            headVH.bindHeading(modelQNSArrayList.get(position));
            headVH.tvHeading.setText(modelQNSArrayList.get(position).getQuestion());

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (modelQNSArrayList != null && modelQNSArrayList.size() > 0) {
           /* if (modelQNSArrayList.get(position).getqType().equals("Rating")) {
                return LAYOUT_STAR;
            } else*/
            if (modelQNSArrayList.get(position).getqType().equals("Module")) {
                return LAYOUT_MODULE;
            } else if (modelQNSArrayList.get(position).getqType().equals("Comment")) {
                return LAYOUT_COMMENT;
            } else if (modelQNSArrayList.get(position).getqType().equals("Heading")) {
                return LAYOUT_HEADING;
            }
            else {
                return LAYOUT_STAR;
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        if (modelQNSArrayList != null && !modelQNSArrayList.isEmpty()) {
            return modelQNSArrayList.size();
        } else {
            return 0;
        }
    }

    public class QuestionAnswerViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvRating, tvQId,tvSrNo;
        RatingBar rbRating;

        private Context mContext;

        public QuestionAnswerViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            tvQuestion = (TextView) itemView.findViewById(R.id.tv_ques);
            tvRating = (TextView) itemView.findViewById(R.id.tv_ques_rating);
            tvQId = (TextView) itemView.findViewById(R.id.tv_ques_id);
            rbRating = (RatingBar) itemView.findViewById(R.id.rb_ques);
            tvSrNo = (TextView) itemView.findViewById(R.id.tv_star_q_sr);
        }

        public void bindQuesAns(ModelQNS modelQNS) {
            if (modelQNS != null) {
                tvQuestion.setText(modelQNS.getQuestion());
                tvQId.setText(modelQNS.getQuesId());
                tvRating.setText(modelQNS.getRating());
                tvSrNo.setText(modelQNS.getSrNo()+") ");
            }
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvComment, tvQId,tvSrNo;
        EditText edComment;
        private Context mContext;

        public CommentViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            tvQId = (TextView) itemView.findViewById(R.id.tv_id_comment);
            tvComment = (TextView) itemView.findViewById(R.id.tv_comment_title);
            edComment = (EditText) itemView.findViewById(R.id.edt_comment);
            tvSrNo = (TextView) itemView.findViewById(R.id.tv_comment_q_sr);
        }

        public void bindComment(ModelQNS modelQNS) {
            if (modelQNS != null) {
                tvComment.setText(modelQNS.getQuestion());
                tvQId.setText(modelQNS.getQuesId());
                edComment.setText(modelQNS.getRating());
                tvSrNo.setText(modelQNS.getSrNo()+") ");
            }
        }
    }

    public class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView tvModuleTitle, tvQId,tvSrNO;
        EditText edModule;

        private Context mContext;

        public ModuleViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            tvQId = (TextView) itemView.findViewById(R.id.tv_id_module);
            tvModuleTitle = (TextView) itemView.findViewById(R.id.tv_module_title);
            edModule = (EditText) itemView.findViewById(R.id.edt_module);
            tvSrNO = (TextView) itemView.findViewById(R.id.tv_module_q_sr);
        }

        public void bindModule(ModelQNS modelQNS) {
            if (modelQNS != null) {
                tvModuleTitle.setText(modelQNS.getQuestion());
                tvQId.setText(modelQNS.getQuesId());
                edModule.setText(modelQNS.getRating());
                tvSrNO.setText(modelQNS.getSrNo()+") ");
            }
        }
    }

    public class HeadingViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeading, tvQId;

        private Context mContext;

        public HeadingViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            tvQId = (TextView) itemView.findViewById(R.id.tv_id_heading);
            tvHeading = (TextView) itemView.findViewById(R.id.tv_heading);
        }

        public void bindHeading(ModelQNS modelQNS) {
            if (modelQNS != null) {
                tvHeading.setText(modelQNS.getQuestion());
                tvQId.setText(modelQNS.getQuesId());
            }
        }
    }
}
