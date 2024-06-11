package main

import (
	"github.com/stretchr/testify/suite"
	"go.temporal.io/sdk/testsuite"
)

type UnitTestSuite struct {
	suite.Suite
	testsuite.WorkflowTestSuite

	env *testsuite.TestWorkflowEnvironment
}

func (s *UnitTestSuite) Test_SimpleWorkflow_Success() {
	s.env.ExecuteWorkflow(Workflow, "test_success")

	s.True(s.env.IsWorkflowCompleted())
	s.NoError(s.env.GetWorkflowError())

	var value string
	s.env.GetWorkflowResult(&value)

	s.Equal("Hello World!", value)
}
