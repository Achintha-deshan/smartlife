package edu.lk.ijse.smartlife_backend.Repository;

import edu.lk.ijse.smartlife_backend.Entity.DailyExpense;
import edu.lk.ijse.smartlife_backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DailyExpenseRepository extends JpaRepository<DailyExpense, Long> {


    @Query("SELECT d FROM DailyExpense d WHERE d.user.userId = :userId " +
            "AND FUNCTION('DATE_FORMAT', d.date, '%Y-%m') = :month")
    List<DailyExpense> findAllByAdminIdAndMonth(@Param("userId") String userId, @Param("month") String month);
}