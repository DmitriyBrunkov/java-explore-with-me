# java-explore-with-me

������ ��� ������ ����������� � �������� ����� �������������� � �� �����������

### ������� �� ���� ��������:

- �������� ������
- ������ ����������

### �������� ������:

��������� �������� ������
API ������� ��������� �� ��� �����:

* **����������������:**
    * /admin/categories POST - ���������� ����� ���������
    * /admin/categories/{catId} DELETE - �������� ���������
    * /admin/categories/{catId} PATCH - ��������� ���������
    * /admin/events GET - ��������� �������
    * /admin/events/{eventId} PATCH - �������������� �������
    * /admin/users GET - ��������� ���������� � �������������
    * /admin/users POST - ���������� ������ ������������
    * /admin/users/{userId} DELETE - �������� ������������
    * /admin/compilations POST - ���������� ����� ��������
    * /admin/compilations/{compId} DELETE - �������� ��������
    * /admin/compilations/{compId} PATCH - �������� ��������
* **���������:**
    * /users/{userId}/events GET - ��������� ������� ������������
    * /users/{userId}/events POST - ���������� ������ �������
    * /users/{userId}/events/{eventId} GET - ��������� ���������� � ������� ������������
    * /users/{userId}/events/{eventId} PATCH - ��������� ������� ������������
    * /users/{userId}/events/{eventId}/requests GET - ��������� ���������� � �������� �� ������� � ������� ������������
    * /users/{userId}/events/{eventId}/requests PATCH - ��������� ������� ������ �� ������� � ������� ������������
    * /users/{userId}/requests GET - ��������� ���������� � ������� ������������
    * /users/{userId}/requests POST - ���������� ������� �� ������������ �� ������� � �������
    * /users/{userId}/requests/{requestId}/cancel PATCH - ������ ������� ������������ �� ������� � �������
* **���������:**
    * /categories GET - ��������� ���������
    * /categories/{catId} GET - ��������� ���������� � ��������� �� � id
    * /compilations GET - ��������� �������� �������
    * /compilations/{compId} GET - ��������� �������� ������� �� ��� id
    * /events GET - ��������� ������� � ������������ ����������
    * /events/{eventId} GET - ��������� ������� �� ��� id

### ������ ����������:

������������ ��������� ������������� � ������� ������� ���������� ����������

**API ������� ����������**:

* /hit POST - ����������� ���������
* /stats GET - ��������� ���������� �� ����������

### �������������� ����������������: �����������

API ��������� �� 3 �����:

* **����������������:**
    * /admin/comments/{commentId} GET - ��������� ����������� �� id
    * /admin/comments/{commentId} PATCH - ��������� ����������� �� id
    * /admin/comments/{commentId} DELETE - �������� ����������� �� id
* **���������:**
    * /users/{userId}/comments GET - ��������� ����� ������������ ������������
    * /users/{userId}/event/{eventId}/comments POST - �������� �����������
    * /users/{userId}/event/{eventId}/comments/{commentId} PATCH - ��������� �����������
    * /users/{userId}/event/{eventId}/comments/{commentId} DELETE - �������� �����������
* **���������:**
    * /events/{eventId}/comments GET - ��������� ������������ �������

[������ �� PR]()