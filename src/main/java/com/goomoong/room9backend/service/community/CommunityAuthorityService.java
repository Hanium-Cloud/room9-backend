package com.goomoong.room9backend.service.community;

import com.goomoong.room9backend.domain.community.CommunityAuthority;
import com.goomoong.room9backend.exception.NoCommunityAuthException;
import com.goomoong.room9backend.repository.community.CommunityAuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityAuthorityService {

    private final CommunityAuthorityRepository caRepository;

    public boolean checkPostAuth(Long id, String sigCode) {
        List<CommunityAuthority> findCa = caRepository.findByUserIdAndSigCodeOrderByCheckInDesc(id, sigCode);

        if(findCa.isEmpty())
            throw new NoCommunityAuthException("권한이 없는 사용자입니다.");
        else
            return true;
    }

    public List<CommunityAuthority> findByUserId(Long id){
        return caRepository.findByUserId(id);
    }

    public CommunityAuthority findByUserIdAndSigCode(Long id, String sigCode){
        List<CommunityAuthority> findCa = caRepository.findByUserIdAndSigCodeOrderByCheckInDesc(id, sigCode);

        if(findCa.isEmpty()){
            throw new NoCommunityAuthException("권한이 없는 사용자입니다.");
        }
        else{
            return findCa.get(0);
        }

    }

    public int peopleNumInCommunity(String sigCode, LocalDateTime checkIn, LocalDateTime checkOut){
        return caRepository.findBySigCodeAndCheckInBeforeOrCheckOutAfter(sigCode, checkOut, checkIn).size();
    }

    @Transactional
    public CommunityAuthority save(CommunityAuthority ca){
        CommunityAuthority savedCa = caRepository.save(ca);
        return savedCa;
    }

}
