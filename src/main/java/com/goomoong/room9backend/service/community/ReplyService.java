package com.goomoong.room9backend.service.community;

import com.goomoong.room9backend.domain.community.Reply;
import com.goomoong.room9backend.exception.NoSuchReplyException;
import com.goomoong.room9backend.repository.community.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    @Transactional
    public Reply save(Reply reply){
        Reply savedReply = replyRepository.save(reply);
        return savedReply;
    }

    @Transactional
    public Reply update(Long id, String content){
        Reply reply = replyRepository.findById(id).orElseThrow(() -> new NoSuchReplyException("존재하지 않는 댓글입니다."));
        reply.update(content);
        return reply;
    }

    @Transactional
    public void delete(Long id){
        replyRepository.deleteById(id);
    }

    public List<Reply> findByPostId(Long postId){
        return replyRepository.findByPostId(postId);
    }

    public Reply findById(Long id) {
        return replyRepository.findById(id).orElseThrow(() -> new NoSuchReplyException("존재하지 않는 댓글입니다."));
    }
}
