package org.jobradar.crawler;

import org.jobradar.entity.AtsPlatform;
import org.jobradar.entity.Company;
import org.jobradar.entity.JobPosting;

import java.util.List;

public interface AtsCrawler {

    List<JobPosting> crawl(String atsJobUrl, Company company, AtsPlatform platform);

}
