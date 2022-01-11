package bar.barinade.livecheck.discord.serverconfig.service;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.data.ServerConfiguration;
import bar.barinade.livecheck.discord.serverconfig.repo.ServerConfigurationRepo;
@Service
public class ServerConfigService {
	
	private static final Logger m_logger = LoggerFactory.getLogger(ServerConfigService.class);

	@Autowired
	private ServerConfigurationRepo configRepo;
	
	public ServerConfiguration getConfig(Long guildId) {
		try {
			return configRepo.getById(guildId);
		} catch (EntityNotFoundException e) {
			ServerConfiguration config = new ServerConfiguration();
			config.setId(guildId);
			return configRepo.saveAndFlush(config);
		}
	}
	
	public void setRequiredTitleRegex(Long guildId, String regex) {
		ServerConfiguration config = getConfig(guildId);
		config.setTitleRegex(regex);
		configRepo.saveAndFlush(config);
		m_logger.info("Guild {} set title regex to {}", guildId, regex);
	}
	
	public void setTextChannel(Long guildId, Long channelId) {
		ServerConfiguration config = getConfig(guildId);
		config.setChannelId(channelId);
		configRepo.saveAndFlush(config);
		m_logger.info("Guild {} set output channel to {}", guildId, channelId);
	}
	
	public void setLiveRole(Long guildId, Long roleId) {
		ServerConfiguration config = getConfig(guildId);
		config.setMentionRoleId(roleId);
		configRepo.saveAndFlush(config);
		m_logger.info("Guild {} set live-mention role to {}", guildId, roleId);
	}
	
}
