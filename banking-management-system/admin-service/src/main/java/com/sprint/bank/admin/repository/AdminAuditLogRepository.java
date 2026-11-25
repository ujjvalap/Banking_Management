
package com.sprint.bank.admin.repository;

import com.sprint.bank.admin.entity.AdminAuditLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminAuditLogRepository
        extends JpaRepository<AdminAuditLog, Long> {

    // Find all audit logs performed by a particular admin
    List<AdminAuditLog> findByAdminId(Long adminId);

    // Find all audit logs related to a particular account
    List<AdminAuditLog> findByAccountId(Long accountId);

    // Find audit logs based on action
    List<AdminAuditLog> findByAction(String action);
}
