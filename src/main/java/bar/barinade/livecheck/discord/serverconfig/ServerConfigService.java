package bar.barinade.livecheck.discord.serverconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bar.barinade.livecheck.discord.serverconfig.repo.BlacklistedCategoryRepo;
import bar.barinade.livecheck.discord.serverconfig.repo.BlacklistedChannelRepo;
import bar.barinade.livecheck.discord.serverconfig.repo.DefinedCategoryRepo;
import bar.barinade.livecheck.discord.serverconfig.repo.DefinedChannelRepo;
import bar.barinade.livecheck.discord.serverconfig.repo.ServerConfigurationRepo;
import bar.barinade.livecheck.discord.serverconfig.repo.WhitelistedCategoryRepo;
import bar.barinade.livecheck.discord.serverconfig.repo.WhitelistedChannelRepo;

@Service
public class ServerConfigService {
	
	private static final Logger m_logger = LoggerFactory.getLogger(ServerConfigService.class);

	@Autowired
	private BlacklistedCategoryRepo blCategoryRepo;
	
	@Autowired
	private BlacklistedChannelRepo blChannelRepo;
	
	@Autowired
	private DefinedCategoryRepo categoryRepo;
	
	@Autowired
	private DefinedChannelRepo channelRepo;
	
	@Autowired
	private WhitelistedCategoryRepo wlCategoryRepo;
	
	@Autowired
	private WhitelistedChannelRepo wlChannelRepo;
	
	@Autowired
	private ServerConfigurationRepo configRepo;
	
}
