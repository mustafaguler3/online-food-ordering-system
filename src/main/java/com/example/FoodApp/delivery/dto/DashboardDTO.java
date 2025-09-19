package com.example.FoodApp.delivery.dto;

import com.example.FoodApp.order.dtos.OrderDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DashboardDTO {
    private long assignedOrders;
    private long completedOrders;
    private BigDecimal todayEarnings;
    private boolean active;
    private List<DailyEarningDTO> dailyEarnings;
}
