<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib tagdir="/WEB-INF/tags/ryctag/" prefix="ryctag" %> 

<html>
<head>
<meta name="Description" lang="fr" content="Vers un nouvel enseignement fondamental et secondaire en Fédération Wallonie-Bruxelles. Des réformes du système scolaire pour des professeurs moins stressés dans des écoles plus sereines et pour des élèves plus instruits et plus heureux." />
<meta name="Keywords" content="enseignement, éducation, élève, professeur, instituteur, institutrice, ressources, études, secondaire, fondamental, technique, professionnel, primaire, cours, pédagogie, classe" />
<meta name="robots" content="index, follow"/>	
<link rel="canonical" href="http://enseignement2.be/"/>
<meta name="googlebot" content="noarchive" />

<!-- CU3ER content JavaScript part starts here   -->
<script type="text/javascript" src="js/ext/swfobject.js"></script>
<script type="text/javascript" src="js/ext/CU3ER.js"></script>
<script type="text/javascript" src="js/ext/home.js"></script>
<!-- CU3ER content JavaScript part ends here   -->
<title>enseignement2.be</title>
</head>
<body>
<!-- ***************** - Homepage 3D Slider - ***************** --><!-- locations where are the pictures -->
<center>
<div id="CU3ER-containter" >
	<!-- CU3ER content HTML part starts here   -->
		<div id="CU3ER" >
		
			<!-- modify this content to provide users without Flash or enabled Javascript 
			   with alternative content information -->
			<p>
			Click to get Flash Player<br />
			<a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" /></a>
			</p>
			<p>or try to enable JavaScript and reload the page</p>
		</div>
</div>
</center>
<!-- ***************** - END Homepage 3D Slider - ***************** -->
<div class="callout-wrap">
	<span>Enseignement2.be rassemble des pistes sur l'amélioration de l'enseignement en Belgique francophone. Ce site est indépendant du politique et de l’Administration.</span>
</div>

<div style="width:450px;"> 
<div class="h7">
Articles dernièrement publiés

</div>
	<c:forEach items="${articleListByDate}" var="art">
		<div class ="listarticle" >
			<div style= "float:left">
				<a href= "article/${art.article.url}">${art.article.title}</a><br/>
				
					<c:choose>
						<c:when test="${art.article.description != null}">
							${art.article.description}						
						</c:when>
						<c:otherwise>
							Pas de description disponible pour cet article.
						</c:otherwise>
					</c:choose>
				
			</div>
			<div style= "float:right">
				<span>${art.difference}</span>
			</div>
		</div>
	</c:forEach>
</div>




<!-- <a href="">Enseignement2.be</a> rassemble des pistes sur l'amélioration de l'enseignement en Belgique francophone. -->

<!-- <p class="subtitle">D'abord, pourquoi changer?</p> -->

<!-- <p>Ce site n'est pas encore destiné au grand public, sans pour autant se cacher. Il est tout à fait officieux et n'est lié à aucun acteur traditionnel (ni politique, ni syndical, ni association de parents). -->
<!-- </p> -->
<!-- <p>Ce site traite de beaucoup de sujets, si bien qu'il n'est pas facile de voir l'essentiel. Pour nous, le plus important à l'heure actuelle, c'est (dans le désordre) de: -->
<!-- </p> -->

<!-- <ul> -->
<!-- <li> -->
<!--     Rendre les écoles plus autonomes, notamment en terme de recrutement/licenciement, et de contrôler les résultats avec plus d'examens centralisés. -->
<!--     </li> -->
<!--     <li> -->
<!--     Décloisonner: polyvalence des enseignants, cours-projets transversaux, flux inter-réseaux. -->
<!--     </li> -->
<!--     <li> -->
<!--     Tirer les forts (inclus ceux parmi les pauvres) vers le haut: classes de niveau, XXXXXX -->
<!--     </li> -->
<!--     <li> -->
<!--     Rendre les élèves autonomes et acteurs (prendre plutôt que subir): stop à la réussite automatique de relégation; pédagogie par projet; moments d'étude individuels avec manuels scolaires auto-didactes; conditionner les activités plaisantes (foot) à la finition des autres (math), participation à la vie de l'école. -->
<!--     </li> -->
<!--     <li> -->
<!--     Faire passer plus de temps en classe(LINK) aux enseignants, à plusieurs, grâce à une réduction des gaspillages, à une baisse du travail hors classe, et à un travail en classe moins pénible. -->
<!--     </li> -->
<!--     <li> -->
<!--     Attirer les meilleurs vers le métier d'enseignant. -->
<!--     </li> -->
<!-- </ul> -->
</body>
</html>