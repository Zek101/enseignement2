package reformyourcountry.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reformyourcountry.exception.InvalidPasswordException;
import reformyourcountry.exception.UserAlreadyExistsException;
import reformyourcountry.exception.UserLockedException;
import reformyourcountry.exception.UserNotFoundException;
import reformyourcountry.exception.UserNotValidatedException;
import reformyourcountry.mail.MailingDelayType;
import reformyourcountry.model.Action;
import reformyourcountry.model.Argument;
import reformyourcountry.model.Article;
import reformyourcountry.model.ArticleVersion;
import reformyourcountry.model.Book;
import reformyourcountry.model.Comment;
import reformyourcountry.model.Group;
import reformyourcountry.model.GroupReg;
import reformyourcountry.model.User;
import reformyourcountry.model.User.AccountConnectedType;
import reformyourcountry.model.User.AccountStatus;
import reformyourcountry.model.User.Gender;
import reformyourcountry.model.User.Role;
import reformyourcountry.model.VoteAction;
import reformyourcountry.model.VoteArgument;
import reformyourcountry.repository.GroupRepository;
import reformyourcountry.repository.UserRepository;
import reformyourcountry.security.Privilege;
import reformyourcountry.service.LoginService;
import reformyourcountry.service.LoginService.WaitDelayNotReachedException;
import reformyourcountry.service.UserService;
import reformyourcountry.util.Logger;
import reformyourcountry.web.ContextUtil;
import reformyourcountry.web.UrlUtil;

@Service
@Transactional
public class BatchCreate implements Runnable {

	@PersistenceContext
	EntityManager em;
	
	@Logger Log log;
	
	@Autowired
	UserService userService;
	@Autowired
	LoginService loginService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	GroupRepository groupRepository;

	public static void main(String[] args) {
		BatchUtil.startSpringBatch(BatchCreate.class);
	}
           
	@SuppressWarnings("unused")
    public void run() {
		BatchCreate proxy = ContextUtil.getSpringBean(BatchCreate.class);

		User user = proxy.populateUsers();
		User user2 = proxy.populateUsersWithOneUser();
	
		proxy.populateUsersWithOneModerator();
	    //proxy.loginUser(user);

		Article article = proxy.populateArticle("Le Web 2.0 et les profs", 
		                                        "web2.0", 
		                                        "article.txt", 
		                                        "1.Échanger pour se former/2.Construire ensemble/3.Du plaisir de la mise en réseau",
		                                        null, null, null, null,
		                                        "2012-09-09",
		                                        false);
		Article article2 = proxy.populateArticle("Autonomie des écoles", 
                                                 "auto", 
                                                 "article2.txt", 
                                                 "1.Échanger pour se former/2.Construire ensemble/3.Du plaisir de la mise en réseau",
                                                 null, null, null, null,
                                                 "2012-09-09",
                                                 false);
		Article article3 = proxy.populateArticle("Directeurs d'école", 
		                                         "dir", 
		                                         "article3.txt", 
		                                         "1.Règles actuelles/2.Décrets pédagogiques/3.Contrôle des résultats/4.Recrutement/Licenciement/5.Libre plus autonome",
		                                         null, null, null, article2,
		                                         "2012-09-09",
		                                         false);
		Article article4 = proxy.populateArticle("Ecole de l'avenir", 
		                                         "avenir", 
		                                         "article4.txt", 
		                                         "1.Règles actuelles/2.Décrets pédagogiques/3.Contrôle des résultats/4.Recrutement/Licenciement/5.Libre plus autonome",
		                                         null, null, null, article3,
		                                         "2012-09-09",
		                                         false);
		Article article5 = proxy.populateArticle("Article effort", 
                                                 "effort", 
                                                 "articleEffort.txt", 
                                                 "1.Règles actuelles/2.Décrets pédagogiques/3.Contrôle des résultats/4.Recrutement/Licenciement/5.Libre plus autonome",
                                                 null, null, null, article3,
                                                 "2012-09-09",
                                                 false);
		
		Article article6 = proxy.populateArticle("Article autonomie", 
                                                 "autonomie", 
                                                 "articleAutonomie.txt", 
                                                 "1.Règles actuelles/2.Décrets pédagogiques/3.Contrôle des résultats/4.Recrutement/Licenciement/5.Libre plus autonome",
                                                 null, null, null, article3,
                                                 "2012-09-09",
                                                 false);
		
		Action action = proxy.populateAction(article);
		proxy.populateComment(action, user);
		Argument argument = proxy.populatedArgument(action, user);
		proxy.populateVoteArgument(argument, user);
		proxy.populateBook();
		proxy.polulateAction();
		
		proxy.populateGroup();
		Group group1 = groupRepository.findByName("cdH");
		Group group2 = groupRepository.findByName("Ecolo");
		proxy.populateGroupReg(user, group1);
		proxy.populateGroupReg(user, group2);
		proxy.populateGroupReg(user2, group2);
		
	}

	// public void run() {
	//
	//
	//
	// User user = populateUsers();
	//
	// loginUser(user);
	//
	// Article article = populateArticle();
	// Action action = populateAction(article);
	// populateComment(action , user);
	// Group group = populateGroup();
	// populateGroupReg(user,group);
	// populateVoteAction(action,user,group);
	// Argument argument = populatedArgument(action,user);
	// populateVoteArgument(argument,user);
	//
	//
	// }
	public void loginUser(User user) {
		try {
			loginService.login(user.getUserName(), "secret", false,user.getId(), AccountConnectedType.LOCAL);
		} catch (UserNotFoundException | InvalidPasswordException
				| UserNotValidatedException | UserLockedException
				| WaitDelayNotReachedException e) {
			throw new RuntimeException(e);
		}

	}

	
	public User populateUsers() {

		User user = null;
		try {
			user = userService.registerUser(true, "test", "secret",
					"test@mail.com", false);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date bithdate;
			try {
				bithdate = sdf.parse("1982-02-23");
				user.setBirthDate(bithdate);
				user.setLastAccess(sdf.parse("2012-07-05"));
				user.setLastFailedLoginDate(sdf.parse("2012-07-10"));
				user.setLastMailSentDate(sdf.parse("2012-07-01"));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			user.setFirstName("Testname");
			user.setLastName("Testlastname");
			user.setGender(Gender.MALE);
			user.setMailDelayType(MailingDelayType.IMMEDIATELY);
			user.setNlSubscriber(true);
			user.setPicture(false);
			user.setRegistrationDate(new Date());
			user.setRole(Role.ADMIN);
			user.setSpammer(false);
			user.setSpamReporter(null);
			user.setAccountStatus(AccountStatus.ACTIVE);
			user.setValidationCode("123456789");
		} catch (UserAlreadyExistsException e) {
			throw new RuntimeException(e);
		}
		return user;

	}

	
	public User populateUsersWithOneModerator() {

		User user = null;
		try {
			user = userService.registerUser(true, "moder", "secret",
					"moder@mail.com", false);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date bithdate;
			try {
				bithdate = sdf.parse("1982-01-29");
				user.setBirthDate(bithdate);
				user.setLastAccess(sdf.parse("2012-07-05"));
				user.setLastFailedLoginDate(sdf.parse("2012-07-10"));
				user.setLastMailSentDate(sdf.parse("2012-07-01"));
			} catch (ParseException e) {

				throw new RuntimeException(e);
			}
			user.setFirstName("Testname");
			user.setLastName("Testlastname");
			user.setGender(Gender.MALE);
			user.setLastLoginIp("192.168.1.7");
			user.setMailDelayType(MailingDelayType.IMMEDIATELY);
			user.setNlSubscriber(true);
			user.setPicture(false);
			user.setRegistrationDate(new Date());
			user.setRole(Role.MODERATOR);
			user.getPrivileges().add(Privilege.EDIT_ARTICLE);
			user.getPrivileges().add(Privilege.MANAGE_USERS);

			user.setSpammer(false);
			user.setSpamReporter(null);
			user.setAccountStatus(AccountStatus.ACTIVE);
			user.setValidationCode("123456789");

			// em.persist(user);
		} catch (UserAlreadyExistsException e) {
			throw new RuntimeException(e);
		}
		return user;

	}

	
	public User populateUsersWithOneUser() {

		User user = null;
		try {
			user = userService.registerUser(true, "user", "secret",
					"user@mail.com", false);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date bithdate;
			try {
				bithdate = sdf.parse("1982-01-30");
				user.setBirthDate(bithdate);
				user.setLastAccess(sdf.parse("2012-07-05"));
				user.setLastFailedLoginDate(sdf.parse("2012-07-10"));
				user.setLastMailSentDate(sdf.parse("2012-07-01"));
			} catch (ParseException e) {

				throw new RuntimeException(e);
			}
			user.setFirstName("Testname");
			user.setLastName("Testlastname");
			user.setGender(Gender.MALE);
			user.setLastLoginIp("192.168.1.8");
			user.setMailDelayType(MailingDelayType.IMMEDIATELY);
			user.setNlSubscriber(true);
			user.setPicture(false);
			user.setRegistrationDate(new Date());
			user.setRole(Role.USER);

			user.setSpammer(false);
			user.setSpamReporter(null);
			user.setAccountStatus(AccountStatus.ACTIVE);
			user.setValidationCode("123456788");

			// em.persist(user);
		} catch (UserAlreadyExistsException e) {
			throw new RuntimeException(e);
		}
		return user;

	}
	
	public Article populateArticle(String title, String shortName, String contentFile, String summary, String toClassify, String description, String url, Article parent,String publishDate, boolean publicView){
	    
	    Path path = FileSystems.getDefault().getPath("src/reformyourcountry/"+contentFile);
	    int ch;
	    String content = "";
	    
	    try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
            while((ch = reader.read()) != -1){
                content += Character.toString((char) ch); 
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	    
	    ArticleVersion articleVersion = new ArticleVersion();
	    articleVersion.setContent(content);
	    articleVersion.setSummary(summary != null ? summary : "Résumé à compléter");
	    articleVersion.setToClassify(toClassify != null ? toClassify : "");
	    
	    Article article = new Article();
	    article.setTitle(title);
	    article.setShortName(shortName);
	    article.setUrl(url != null ? url : UrlUtil.computeUrlFragmentFromName(title));
	    article.setDescription(description != null? description : summary);
	    article.setParent(parent);
	    article.setPublicView(publicView);
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            article.setPublishDate(sdf.parse(publishDate));
        } catch (ParseException e) {

            throw new RuntimeException(e);
        }
	    
	    
	    
	    
	    return populateArticle(article,articleVersion);
	}
	

	
	public Article populateArticle(Article article,ArticleVersion articleVersion) {
	    
		em.persist(article);
		articleVersion.setArticle(article);
		
		em.persist(articleVersion);
		article.setLastVersion(articleVersion);
		
		em.merge(article);

		return article;
	}
	
	
	public Action populateAction(Article article) {

		Action action = new Action("Donner des Ipad 3 à tous les élèves",
				"l'interaction virtuelle dynamique dans l'aprentissage.....");

		action.setContent("this is the content of the action");
		action.setUrl(UrlUtil.computeUrlFragmentFromName(action.getTitle()));

		action.addArticle(article);

		em.persist(action);
		article.addAction(action);
		em.merge(article);
		return action;

	}

	
	public Comment populateComment(Action action, User user) {

		Comment comment = new Comment("oui mais non",
				"je ne suis pas d'accord ", action, user);
		action.addComment(comment);

		em.persist(comment);
		em.merge(action);

		return comment;

	}

	
	public VoteAction populateVoteAction(Action action, User user, Group group) {

		VoteAction voteAction = new VoteAction(1, action, user, group);
		em.persist(voteAction);
		group.addVoteAction(voteAction);
		em.merge(group);

		return voteAction;
	}

	
	public void populateGroup() {
		Group group1 = new Group();
		Group group2 = new Group();
		Group group3 = new Group();
		Group group4 = new Group();

		group1.setDescription("Parti Socialiste");
		group1.setName("PS");
		group1.setUrl("parti-socialiste");

		group2.setDescription("Centre démocrate humaniste");
		group2.setName("cdH");
		group2.setUrl("centre-democrate-humaniste");

		group3.setDescription("Mouvement réformateur");
		group3.setName("MR");
		group3.setUrl("mouvement-reformateur");

		group4.setDescription("Parti écologiste");
		group4.setName("Ecolo");
		group4.setUrl("parti-ecologiste");

		em.persist(group1);
		em.persist(group2);
		em.persist(group3);
		em.persist(group4);
	}

	
	
	
	
	
	public GroupReg populateGroupReg(User user, Group group) {

		GroupReg groupReg = new GroupReg();

		
		groupReg.setUser(user);
		groupReg.setGroup(group);

		//em.persist(groupReg);
		em.merge(groupReg);
		
		return groupReg;
	}

	
	public Argument populatedArgument(Action action, User user) {

		Argument argument = new Argument("non", "il est demontré que....",
				action, user);
		action.addArgument(argument);

		em.persist(argument);
		em.merge(action);

		return argument;
	}

	
	public VoteArgument populateVoteArgument(Argument argument, User user) {

		VoteArgument voteArgument = new VoteArgument(2, argument, user);
		em.persist(voteArgument);
		argument.addVoteArgument(voteArgument);
		em.merge(argument);

		return voteArgument;

	}

	
	public void populateBook() {

		Book book1 = new Book(
				"abcd",
				"Les clés du succès des systèmes scolaires les plus performants",
				UrlUtil.computeUrlFragmentFromName("Les clés du succès des systèmes scolaires les plus performants"),
				"Excellent rapport, agréable à lire par tous, pour comprendre les différences entre systèmes scolaires dans le monde et ce qui fait que certains s'améliorent.",
				"McKinsey",
				"2007",
				true,
				"http://mckinseyonsociety.com/how-the-worlds-best-performing-schools-come-out-on-top/");
		Book book2 = new Book(
				"mens",
				"Un meilleur enseignement en Communauté française; nous le pouvons si nous le voulons",
				UrlUtil.computeUrlFragmentFromName("Un meilleur enseignement en Communauté française; nous le pouvons si nous le voulons"),
				"Facultés Universitaires Notre Dame de la Paix - Namur",
				"Robert Deschamps", "2010", false,
				"http://www.fundp.ac.be/pdf/publications/70749.pdf");
		Book book3 = new Book(
				"cede",
				"Réflexions en vue d'un système éducatif plus performant pour tous les enfants",
				UrlUtil.computeUrlFragmentFromName("Réflexions en vue d'un système éducatif plus performant pour tous les enfants"),
				"Centre d'étude de de défense de l'école publique.", "", "",
				false, "http://www.cedep.be/default.asp?contentID=31");
		Book book4 = new Book(
				"ecol",
				"Ecole de l'échec: comment la réformer?",
				UrlUtil.computeUrlFragmentFromName("Ecole de l'échec: comment la réformer?"),
				"Du pédagogisme à la gouvernance",
				"Alain Destexhe, Vincent Vandenberghe, Guy Vlaeminck",
				"2004",
				false,
				"http://www.bookfinder.com/dir/i/Lecole_De_Lechec-Comment_La_Reformer-Du_Pedagogisme_a_La_Gouvernance/2804019322/");
		Book book5 = new Book("muta", "La mutation de l'école secondaire",
				UrlUtil.computeUrlFragmentFromName("La mutation de l'école secondaire"),
				"Questions de sens - Propositions d’action",
				"Francis Tilman, Dominique Grootaers, Barbara Dufour", "2011",
				true,
				"http://www.couleurlivres.be/html/nouveautes/mutation-ecol-sec.html");

		em.persist(book1);
		em.persist(book2);
		em.persist(book3);
		em.persist(book4);
		em.persist(book5);

	}

	
	public void polulateAction() {
		Action action1 = new Action();
		Action action2 = new Action();
		Action action3 = new Action();

		action1.setTitle("Statut du Directeur");
		action1.setShortDescription("Sanction contre un directeur d'école!");
		action1.setContent("L’Inspection académique de l’Isère a décidé de démettre de ses fonctions un directeur d’école primaire, tout en lui conservant sa fonction d’instituteur. Jean-Yves Le Gall, directeur de l’école primaire de Notre-Dame-de-Vaulx, refusait d’enregistrer des informations dans la banque de données “base élèves”.");
		action1.setUrl(UrlUtil.computeUrlFragmentFromName(action1.getTitle()));

		action2.setTitle("Les droits et obligations des enseignants");
		action2.setContent("Les enseignants bénéficient de droits liés aux missions qu'ils exercent, mais aussi d'un certain nombre d'obligations. Chaque membre de la communauté éducative doit avoir un comportement et une conduite irréprochable vis-à-vis des élèves, de ses collègues et de l'environnement scolaire dans lequel il se trouve. ");
		action2.setUrl(UrlUtil.computeUrlFragmentFromName(action2.getTitle()));

		action3.setTitle("Les obligations enseignants");
		action3.setShortDescription("L'obligation d'obéissance hiérarchique");
		action3.setContent("L'enseignant doit toujours se conformer aux instructions de son supérieur hiérarchique, sauf dans le cas où l'ordre donné est manifestement illégal et de nature à compromettre gravement un intérêt public. Le refus d'obéissance est considéré comme une faute professionnelle. En outre, l'enseignant se doit de respecter les lois et règlements de toute nature.");
		action3.setUrl(UrlUtil.computeUrlFragmentFromName(action3.getTitle()));

		em.persist(action1);
		em.persist(action2);
		em.persist(action3);
	}

}
