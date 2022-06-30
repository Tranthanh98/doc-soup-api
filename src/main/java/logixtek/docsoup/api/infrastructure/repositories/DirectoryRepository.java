package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.models.ContentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DirectoryRepository extends JpaRepository<DirectoryEntity, Long> {

    Optional<DirectoryEntity> findFirstByNameAndParentIdAndCompanyIdAndIsTeamTrue(String name,long parentId,UUID companyId);

    Optional<List<DirectoryEntity>> findAllByParentId(Long parentId);

    Optional<List<DirectoryEntity>> findAllByCompanyIdAndIsTeamTrue(UUID companyId);
    Optional<List<DirectoryEntity>> findAllByAccountIdAndCompanyIdAndIsTeamFalse(String accountId,UUID companyId);

    @Procedure(name = "DirectoryEntity.addWithParent")
    Optional<Long> addWithParentId(@Param("parentId") long parentId,
                                   @Param("name") String name,
                                   @Param("accountId") String accountId,
                                   @Param("companyId") String companyId);

    @Procedure(name = "DirectoryEntity.moveDirectory")
    Optional<Integer> move(@Param("parentId") long parentId,
                           @Param("id") Long id,
                                   @Param("companyId") String companyId,
                           @Param("accountId") String accountId,
                           @Param("isTeam") Boolean isTeam);

    Optional<DirectoryEntity> findByIdAndAccountId(Long directoryId, String accountId);

    @Query(value = "EXECUTE [dbo].[sel_children_of_parentId]"
           + " :parentId", nativeQuery = true)
    List<Long> findAllChildrenOfParent(@Param("parentId") Long parentId);

    @Query(value = "EXECUTE [dbo].[sel_secure_id_in_directory] :directoryId", nativeQuery = true)
    Optional<List<String>> findAllSecureIdsInDirectory(@Param("directoryId") Long directoryId);

    @Procedure(name = "DirectoryEntity.delete")
    int deleteAllById(@Param("directoryId") Long directoryId);

    Optional<DirectoryEntity> findFirstByAccountIdAndParentIdAndCompanyIdAndIsTeam(String accountId, Long parentId, UUID companyId, boolean isTeam);

    Optional<DirectoryEntity> findFirstByNameAndAccountIdAndParentIdAndCompanyIdAndIsTeamFalse(String name, String accountId, long parentId, UUID companyId);

    Integer countAllByAccountIdAndParentIdAndCompanyIdAndIsTeamIsFalse(String accountId, Long parentId, UUID companyId);

    Optional<DirectoryEntity> findFirstByAccountIdAndCompanyIdAndIsTeamFalse(String accountId, UUID companyId);

    Optional<DirectoryEntity> findFirstByAccountIdAndCompanyIdAndIsTeamTrue(String accountId, UUID companyId);

    @Query(value = "EXECUTE [dbo].[sel_directory_by_keyword] :keyword, :companyId, :accountId, :page, :pageSize", nativeQuery = true)
    Optional<Collection<ContentResult>> findDirectoriesWithKeyword(@Param("keyword") String keyword, @Param("companyId") String companyId, @Param("accountId") String accountId, @Param("page") Integer page, @Param("pageSize") Integer pageSize);

    @Query(value = "with cte " +
            "as ( " +
            "    select * from directory_entity with(nolock) " +
            "    where parent_id = :directoryId " +
            "    UNION ALL " +
            "    SELECT a.* from directory_entity as a  with(nolock), cte as b " +
            "    WHERE a.parent_id = b.id " +
            ") " +
            "SELECT * from cte;"
            , nativeQuery = true)
    Collection<DirectoryEntity> findAllChildrenByDirectoryId(Long directoryId);
}
