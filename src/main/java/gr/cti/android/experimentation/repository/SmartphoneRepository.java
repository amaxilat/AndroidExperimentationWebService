package gr.cti.android.experimentation.repository;

import gr.cti.android.experimentation.model.Smartphone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;


/**
 * @author Dimitrios Amaxilatis.
 */
public interface SmartphoneRepository extends CrudRepository<Smartphone, Long> {

    Page<Smartphone> findAll(Pageable pageable);

    Smartphone findById(int id);

    Smartphone findByPhoneId(long phoneId);
}