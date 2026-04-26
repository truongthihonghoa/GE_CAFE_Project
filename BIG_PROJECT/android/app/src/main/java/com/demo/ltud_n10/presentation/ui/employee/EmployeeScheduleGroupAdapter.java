package com.demo.ltud_n10.presentation.ui.employee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.ltud_n10.databinding.ItemEmployeeScheduleGroupBinding;
import com.demo.ltud_n10.domain.model.WorkShift;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class EmployeeScheduleGroupAdapter extends RecyclerView.Adapter<EmployeeScheduleGroupAdapter.ViewHolder> {

    private List<Calendar> days = new ArrayList<>();
    private Map<String, List<WorkShift>> groupedShifts = new TreeMap<>();
    private final SimpleDateFormat keyFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private final SimpleDateFormat fullDateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
    private final SimpleDateFormat dayOfMonthFormat = new SimpleDateFormat("d", Locale.getDefault());

    public void setWeekData(Calendar startOfWeek, List<WorkShift> shifts) {
        days.clear();
        groupedShifts.clear();

        // Initialize 7 days of the week
        Calendar cal = (Calendar) startOfWeek.clone();
        for (int i = 0; i < 7; i++) {
            Calendar day = (Calendar) cal.clone();
            days.add(day);
            groupedShifts.put(keyFormat.format(day.getTime()), new ArrayList<>());
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Group shifts by date
        if (shifts != null) {
            // Định dạng ngày từ API là yyyy-MM-dd
            SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            for (WorkShift shift : shifts) {
                try {
                    if (shift.getDate() == null) continue;
                    
                    Calendar sCal = Calendar.getInstance();
                    sCal.setTime(apiDateFormat.parse(shift.getDate()));
                    String key = keyFormat.format(sCal.getTime());
                    
                    if (groupedShifts.containsKey(key)) {
                        groupedShifts.get(key).add(shift);
                    }
                } catch (Exception e) {
                    // Thử định dạng khác nếu dd/MM/yyyy
                    try {
                        SimpleDateFormat altDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Calendar sCal = Calendar.getInstance();
                        sCal.setTime(altDateFormat.parse(shift.getDate()));
                        String key = keyFormat.format(sCal.getTime());
                        if (groupedShifts.containsKey(key)) {
                            groupedShifts.get(key).add(shift);
                        }
                    } catch (Exception ignored) {}
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEmployeeScheduleGroupBinding binding = ItemEmployeeScheduleGroupBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(days.get(position));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEmployeeScheduleGroupBinding binding;

        ViewHolder(ItemEmployeeScheduleGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Calendar cal) {
            String key = keyFormat.format(cal.getTime());
            List<WorkShift> shifts = groupedShifts.get(key);

            binding.tvDayOfMonth.setText(dayOfMonthFormat.format(cal.getTime()));
            binding.tvFullDate.setText(fullDateFormat.format(cal.getTime()));
            
            String dayOfWeekStr = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("vi", "VN"));
            binding.tvDayOfWeek.setText(dayOfWeekStr);
            
            int dow = cal.get(Calendar.DAY_OF_WEEK);
            String prefix = dow == Calendar.SUNDAY ? "CN" : "T" + dow;
            binding.tvDayPrefix.setText(prefix);

            if (shifts == null || shifts.isEmpty()) {
                binding.tvNoShift.setVisibility(View.VISIBLE);
                binding.rvShiftsInDay.setVisibility(View.GONE);
            } else {
                binding.tvNoShift.setVisibility(View.GONE);
                binding.rvShiftsInDay.setVisibility(View.VISIBLE);
                
                EmployeeShiftSimpleAdapter adapter = new EmployeeShiftSimpleAdapter();
                binding.rvShiftsInDay.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                binding.rvShiftsInDay.setAdapter(adapter);
                adapter.setItems(shifts);
            }
        }
    }
}
