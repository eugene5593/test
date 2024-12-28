package ru.eugene.planningpoker.impl.converter;

import org.springframework.stereotype.Service;
import ru.eugene.planningpoker.api.DTO.PokerUserDTO;
import ru.eugene.planningpoker.api.entity.PokerUser;

@Service
public class ConverterPokerUsers {

    public PokerUserDTO convertPokerUserToPokerUserDTO(final PokerUser pokerUser) {
        return new PokerUserDTO(pokerUser.getId(),
                pokerUser.getUserId(),
                pokerUser.getName(),
                pokerUser.getSelectedNumber(),
                pokerUser.isLeader(),
                pokerUser.getJoinedAt(),
                pokerUser.getMood());
    }
}
