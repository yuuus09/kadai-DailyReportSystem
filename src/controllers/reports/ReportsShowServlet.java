package controllers.reports;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsShowServlet
 */
@WebServlet("/reports/show")
public class ReportsShowServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsShowServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        Report r = em.find(Report.class, Integer.parseInt(request.getParameter("id")));

        em.close();

        request.setAttribute("report", r);
        request.setAttribute("_token", request.getSession().getId());
        request.getSession().setAttribute("report_id", r.getId());

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/show.jsp");
        rd.forward(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        EntityManager em = DBUtil.createEntityManager();

        // セッションスコープからリポートのIDを取得して
        // 該当のIDのリポート1件のみをデータベースから取得
        Report r = em.find(Report.class, (Integer)(request.getSession().getAttribute("report_id")));


        // いいね！をプロパティに上書き
        Integer likes =  r.getLikes() + 1;
        r.setLikes(likes);

        // データベースを更新
        em.getTransaction().begin();
        em.getTransaction().commit();
        request.getSession().setAttribute("flush", "いいね！しました。");
        em.close();

        // セッションスコープ上の不要になったデータを削除
        request.getSession().removeAttribute("report_id");

        // indexページへリダイレクト
        response.sendRedirect(request.getContextPath() + "/reports/index");

    }

}
