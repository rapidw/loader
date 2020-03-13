import React, {useState} from "react";
import {Button, Card, Col, Form, Input, InputNumber, Modal, Row, Table, message} from "antd";
import {PageHeaderWrapper} from '@ant-design/pro-layout';
import {useForm} from "antd/es/form/util";
import {useRequest} from "@umijs/hooks";

const AgentList: React.FC = () => {
  const columns = [
    {
      title: 'Name',
      dataIndex: 'name',
    },
    {
      title: 'Age',
      dataIndex: 'age',
    },
    {
      title: 'Address',
      dataIndex: 'address',
    },
  ];

  const data = [];
  for (let i = 0; i < 46; i++) {
    data.push({
      key: i,
      name: `Edward King ${i}`,
      age: 32,
      address: `London, Park Lane no. ${i}`,
    });
  }

  const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);
  const [fff, setLoading] = useState<boolean>(false);

  const start = () => {
    setLoading(true)
    // ajax request after empty completing
    setTimeout(() => {
      setLoading(false);
      setSelectedRowKeys([])
    }, 1000);
  };

  const onSelectChange = (selectedRowKeys: any) => {
    console.log('selectedRowKeys changed: ', selectedRowKeys);
    setSelectedRowKeys(selectedRowKeys);
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: onSelectChange,
  };
  const hasSelected = selectedRowKeys.length > 0;

  const [addSupervisorModalVisible, setAddSupervisorModalVisible] = useState(false);

  const onAddSupervisorButtonClicked = () => {
    setAddSupervisorModalVisible(true)
  };

  const [addAgentForm] = useForm();



  const onAddSupervisorModalCancel = () => {
    setAddSupervisorModalVisible(false);
  };

  const addSupervisorInitialValues = {
    port:22,
    path: "~"
  };

  const { loading, run } = useRequest("/api/rrr", {
    manual: true,
    onSuccess: (result, params) => {

      if (result.errorCode == 0) {
      //   console.log("success");
      //   message.success(`new supervisor ${}`);
      }
    }
  });

  const onAddSupervisorFinish = () => {
    run()
  };

  const onAddSupervisorModalOk = () => {

    addAgentForm.validateFields().then(value => {
      addAgentForm.resetFields();
      setAddSupervisorModalVisible(false);
      run()
    }).catch(info => console.log("validate failed", info));
  };

  return (
    <PageHeaderWrapper>
      <Card>
        <Row justify={"space-between"}>
          <Col>
            <div style={{display: "inline-block"}}>
            <Button onClick={start} disabled={!hasSelected} loading={loading}>
              Reload
            </Button>

            <span style={{marginLeft: 8}}>
            {hasSelected ? `Selected ${selectedRowKeys.length} items` : ''}
            </span>
            </div>
          </Col>

          <Col>
            <Button type="primary" onClick={onAddSupervisorButtonClicked}>Add new supervisor</Button>
            <Modal
              title="Supervisor Deploy Config"
              visible={addSupervisorModalVisible}
              onOk={onAddSupervisorModalOk}
              onCancel={onAddSupervisorModalCancel}
              // wrapProps={{style: {pointerEvents: "none"}}}
            >
              <Form form={addAgentForm}
                    labelCol={{lg:5}}
                    wrapperCol={{lg:18}}
                    initialValues={addSupervisorInitialValues}
                    onFinish={onAddSupervisorFinish}
              >
                <Form.Item name="host" label="Host" required >
                  <Input/>
                </Form.Item>
                <Form.Item name="port" label="Port" required >
                  <InputNumber/>
                </Form.Item>
                <Form.Item name="path" label="Deploy path" required >
                  <Input/>
                </Form.Item>
                <Form.Item name="username" label="Username" required >
                  <Input/>
                </Form.Item>
                <Form.Item name="password" label="Password" required >
                  <Input type="password"/>
                </Form.Item>
              </Form>
            </Modal>
          </Col>
        </Row>
        <Row style={{marginTop: "16px"}}>
          <Col span={24}>
            <Table rowSelection={rowSelection} columns={columns} dataSource={data}/>
          </Col>
        </Row>
      </Card>
    </PageHeaderWrapper>
  )
}

export default AgentList;
