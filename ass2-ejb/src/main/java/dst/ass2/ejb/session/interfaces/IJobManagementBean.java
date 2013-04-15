package dst.ass2.ejb.session.interfaces;

import java.util.List;

import dst.ass2.ejb.dto.AssignmentDTO;
import dst.ass2.ejb.session.exception.AssignmentException;

public interface IJobManagementBean {

	/**
	 * Adds a job with the given parameters to the temporary job list if there
	 * are enough free computing resources (i.e., CPUs) for the given grid left.
	 * 
	 * @param gridId
	 * @param numCPUs
	 * @param workflow
	 * @param params
	 * @throws AssignmentException
	 *             if the given grid does not exist or if there are not enough
	 *             free computing resources for the given grid left.
	 */
	public void addJob(Long gridId, Integer numCPUs, String workflow,
			List<String> params) throws AssignmentException;

	/**
	 * @return the list of temporary assigned jobs.
	 */
	public List<AssignmentDTO> getCache();

	/**
	 * Removes temporary assigned jobs for the given gridId.
	 * 
	 * @param gridId
	 */
	public void removeJobsForGrid(Long gridId);

	/**
	 * Allows the user to login.
	 * 
	 * @param username
	 * @param password
	 * @throws AssignmentException
	 *             if the user does not exist or the given username/password
	 *             combination does not match.
	 */
	public void login(String username, String password)
			throws AssignmentException;

	/**
	 * Final submission of the temporary assigned jobs.
	 * 
	 * @throws AssignmentException
	 *             if the user is not logged in or one of the jobs can't be
	 *             assigned anymore.
	 */
	@javax.ejb.Remove // @remove-line@
	public void submitAssignments() throws AssignmentException;

}
