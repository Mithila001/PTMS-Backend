package com.tritonptms.ptms.feature.bus.audit;

import com.tritonptms.ptms.feature.bus.audit.dto.BusRevisionResponse;
import com.tritonptms.ptms.feature.bus.BusMapper;
import com.tritonptms.ptms.feature.bus.BusRepository;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import org.springframework.data.history.Revision;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tritonptms.ptms.infrastructure.audit.CustomRevisionEntity;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BusAuditService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;

    public BusAuditService(BusRepository busRepository, BusMapper busMapper) {
        this.busRepository = busRepository;
        this.busMapper = busMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BusRevisionResponse> getRevisions(Long busId) {
        List<BusRevisionResponse> revisions = busRepository.findRevisions(busId).stream()
                .map(this::toResponse)
                .toList();
        if (revisions.isEmpty()) {
            throw new ResourceNotFoundException("No revision history found for bus id: " + busId);
        }
        return revisions;
    }

    private BusRevisionResponse toResponse(Revision<Integer, com.tritonptms.ptms.feature.bus.Bus> revision) {
        Object delegate = revision.getMetadata().getDelegate();
        String modifiedBy = delegate instanceof CustomRevisionEntity custom ? custom.getUsername() : null;
        return new BusRevisionResponse(
                revision.getRequiredRevisionNumber(),
                revision.getRequiredRevisionInstant(),
                modifiedBy,
                revision.getMetadata().getRevisionType().name(),
                busMapper.toResponse(revision.getEntity()));
    }
}
