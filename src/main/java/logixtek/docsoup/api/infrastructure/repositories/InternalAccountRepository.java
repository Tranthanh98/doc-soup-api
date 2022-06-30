package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.InternalAccountEntity;
import logixtek.docsoup.api.infrastructure.models.SummaryItemViewModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface InternalAccountRepository extends JpaRepository<InternalAccountEntity, String> {

    @Query(value = "EXECUTE [dbo].[sel_summary_dashboard]", nativeQuery = true)
    Collection<SummaryItemViewModel> getSummary();

}
