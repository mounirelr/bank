package ma.bank.repository;

import ma.bank.entities.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRepository  extends JpaRepository<Credit, Long> {

}
