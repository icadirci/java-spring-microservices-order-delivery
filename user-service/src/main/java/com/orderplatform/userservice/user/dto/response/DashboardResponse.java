package com.orderplatform.userservice.user.dto.response;

import com.orderplatform.userservice.auth.dto.response.UserMeResponse;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        long totalUsers,
        long adminCount,
        long newUsersLast30Days,
        long totalOrders,
        BigDecimal totalRevenue,
        List<UserMeResponse> recentUsers
) {
}
