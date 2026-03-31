package edu.lk.ijse.smartlife_backend.Repository;

import edu.lk.ijse.smartlife_backend.Entity.RecurringExpense;
import edu.lk.ijse.smartlife_backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {
    List<RecurringExpense> findAllByIsActiveTrueAndUser(User user);}
