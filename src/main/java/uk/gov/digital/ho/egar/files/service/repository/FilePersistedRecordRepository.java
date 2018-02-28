package uk.gov.digital.ho.egar.files.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uk.gov.digital.ho.egar.files.service.repository.model.FilePersistedRecord;

import javax.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
public interface FilePersistedRecordRepository extends JpaRepository<FilePersistedRecord, UUID>{


	FilePersistedRecord findOneByFileUuidAndUserUuidAndDeletedIsFalse(UUID fileUuid, UUID userUuid);

	@Modifying
	@Query("UPDATE FilePersistedRecord f SET f.deleted = ?2  WHERE f.fileUuid = ?1")
	void updateDeleteStatus(@Param("fileUuid") UUID fileUuid, @Param("deleted") Boolean deleted);

	FilePersistedRecord findOneByFileUuid(UUID fileUuid);

	List<FilePersistedRecord> findAllByUserUuidAndFileUuidIn(UUID uuidOfUser, List<UUID> fileUuids);


}